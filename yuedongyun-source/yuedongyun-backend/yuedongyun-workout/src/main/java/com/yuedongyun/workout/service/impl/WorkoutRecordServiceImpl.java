package com.yuedongyun.workout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.api.client.training.TrainingClient;
import com.yuedongyun.api.dto.training.TrainingFullInfoDTO;
import com.yuedongyun.api.dto.workout.WorkoutSessionDTO;
import com.yuedongyun.api.dto.workout.WorkoutRecordDTO;
import com.yuedongyun.common.exceptions.BizIllegalException;
import com.yuedongyun.common.exceptions.DbException;
import com.yuedongyun.common.utils.BeanUtils;
import com.yuedongyun.common.utils.UserContext;
import com.yuedongyun.workout.domain.dto.WorkoutRecordFormDTO;
import com.yuedongyun.workout.domain.po.WorkoutSession;
import com.yuedongyun.workout.domain.po.WorkoutRecord;
import com.yuedongyun.workout.enums.SessionStatus;
import com.yuedongyun.workout.enums.SessionType;
import com.yuedongyun.workout.mapper.WorkoutRecordMapper;
import com.yuedongyun.workout.service.IWorkoutSessionService;
import com.yuedongyun.workout.service.IWorkoutRecordService;
import com.yuedongyun.workout.utils.WorkoutRecordDelayTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 跟练记录表 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2022-12-10
 */
@Service
@RequiredArgsConstructor
public class WorkoutRecordServiceImpl extends ServiceImpl<WorkoutRecordMapper, WorkoutRecord> implements IWorkoutRecordService {

    private final IWorkoutSessionService sessionService;

    private final TrainingClient trainingClient;

    private final WorkoutRecordDelayTaskHandler taskHandler;

    @Override
    public WorkoutSessionDTO queryWorkoutRecordByTraining(Long trainingId) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();
        // 2.查询课表
        WorkoutSession session = sessionService.queryByUserAndTrainingId(userId, trainingId);
        // 3.查询跟练记录
        // select * from xx where session_id = #{sessionId}
        List<WorkoutRecord> records = lambdaQuery().eq(WorkoutRecord::getSessionId, session.getId()).list();
        // 4.封装结果
        WorkoutSessionDTO dto = new WorkoutSessionDTO();
        dto.setId(session.getId());
        dto.setLatestSessionId(session.getLatestSessionId());
        dto.setRecords(BeanUtils.copyList(records, WorkoutRecordDTO.class));
        return dto;
    }

    @Override
    @Transactional
    public void addWorkoutRecord(WorkoutRecordFormDTO recordDTO) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();
        // 2.处理跟练记录
        boolean finished = false;
        if (recordDTO.getSessionType() == SessionType.VIDEO) {
            // 2.1.处理视频
            finished = handleVideoRecord(userId, recordDTO);
        }else{
            // 2.2.处理考核
            finished = handleExamRecord(userId, recordDTO);
        }
        if(!finished){
            // 没有新学完的小节，无需更新课表中的跟练进度
            return;
        }
        // 3.处理课表数据
        handleWorkoutSessionsChanges(recordDTO);
    }

    private void handleWorkoutSessionsChanges(WorkoutRecordFormDTO recordDTO) {
        // 1.查询课表
        WorkoutSession session = sessionService.getById(recordDTO.getSessionId());
        if (session == null) {
            throw new BizIllegalException("训练不存在，无法更新数据！");
        }
        // 2.判断是否有新的完成小节
        boolean allLearned = false;

            // 3.如果有新完成的小节，则需要查询训练数据
            TrainingFullInfoDTO cInfo = trainingClient.getTrainingInfoById(session.getTrainingId(), false, false);
            if (cInfo == null) {
                throw new BizIllegalException("训练不存在，无法更新数据！");
            }
            // 4.比较训练是否全部学完：已跟练小节 >= 训练总小节
            allLearned = session.getLearnedSessions() + 1 >= cInfo.getSessionNum();

        // 5.更新课表
        sessionService.lambdaUpdate()
                .set(session.getLearnedSessions() == 0, WorkoutSession::getStatus, SessionStatus.WORKOUT.getValue())
                .set(allLearned, WorkoutSession::getStatus, SessionStatus.FINISHED.getValue())
                .setSql("learned_sessions = learned_sessions + 1")
                .eq(WorkoutSession::getId, session.getId())
                .update();
    }

    private boolean handleVideoRecord(Long userId, WorkoutRecordFormDTO recordDTO) {
        // 1.查询旧的跟练记录
        WorkoutRecord old = queryOldRecord(recordDTO.getSessionId(), recordDTO.getSessionId());
        // 2.判断是否存在
        if (old == null) {
            // 3.不存在，则新增
            // 3.1.转换PO
            WorkoutRecord record = BeanUtils.copyBean(recordDTO, WorkoutRecord.class);
            // 3.2.填充数据
            record.setUserId(userId);
            // 3.3.写入数据库
            boolean success = save(record);
            if (!success) {
                throw new DbException("新增跟练记录失败！");
            }
            return false;
        }
        // 4.存在，则更新
        // 4.1.判断是否是第一次完成
        boolean finished = !old.getFinished() && recordDTO.getMoment() * 2 >= recordDTO.getDuration();
        if(!finished){
            WorkoutRecord record = new WorkoutRecord();
            record.setSessionId(recordDTO.getSessionId());
            record.setSessionId(recordDTO.getSessionId());
            record.setMoment(recordDTO.getMoment());
            record.setId(old.getId());
            record.setFinished(old.getFinished());
            taskHandler.addWorkoutRecordTask(record);
            return false;
        }
        // 4.2.更新数据
        boolean success = lambdaUpdate()
                .set(WorkoutRecord::getMoment, recordDTO.getMoment())
                .set(WorkoutRecord::getFinished, true)
                .set(WorkoutRecord::getFinishTime, recordDTO.getCommitTime())
                .eq(WorkoutRecord::getId, old.getId())
                .update();
        if(!success){
            throw new DbException("更新跟练记录失败！");
        }
        // 4.3.清理缓存
        taskHandler.cleanRecordCache(recordDTO.getSessionId(), recordDTO.getSessionId());
        return true;
    }

    private WorkoutRecord queryOldRecord(Long sessionId, Long sessionId) {
        // 1.查询缓存
        WorkoutRecord record = taskHandler.readRecordCache(sessionId, sessionId);
        // 2.如果命中，直接返回
        if (record != null) {
            return record;
        }
        // 3.未命中，查询数据库
        record = lambdaQuery()
                .eq(WorkoutRecord::getSessionId, sessionId)
                .eq(WorkoutRecord::getSessionId, sessionId)
                .one();
        // 4.写入缓存
        taskHandler.writeRecordCache(record);
        return record;
    }

    private boolean handleExamRecord(Long userId, WorkoutRecordFormDTO recordDTO) {
        // 1.转换DTO为PO
        WorkoutRecord record = BeanUtils.copyBean(recordDTO, WorkoutRecord.class);
        // 2.填充数据
        record.setUserId(userId);
        record.setFinished(true);
        record.setFinishTime(recordDTO.getCommitTime());
        // 3.写入数据库
        boolean success = save(record);
        if (!success) {
            throw new DbException("新增考核记录失败！");
        }
        return true;
    }
}

