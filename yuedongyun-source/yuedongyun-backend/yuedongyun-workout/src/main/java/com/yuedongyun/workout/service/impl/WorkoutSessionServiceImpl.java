package com.yuedongyun.workout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.api.client.training.OutlineClient;
import com.yuedongyun.api.client.training.TrainingClient;
import com.yuedongyun.api.dto.IdAndNumDTO;
import com.yuedongyun.api.dto.training.OutlineSimpleInfoDTO;
import com.yuedongyun.api.dto.training.TrainingFullInfoDTO;
import com.yuedongyun.api.dto.training.TrainingSimpleInfoDTO;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.common.domain.query.PageQuery;
import com.yuedongyun.common.exceptions.BadRequestException;
import com.yuedongyun.common.utils.*;
import com.yuedongyun.workout.domain.po.WorkoutSession;
import com.yuedongyun.workout.domain.po.WorkoutRecord;
import com.yuedongyun.workout.domain.vo.WorkoutSessionVO;
import com.yuedongyun.workout.domain.vo.WorkoutPlanPageVO;
import com.yuedongyun.workout.domain.vo.WorkoutPlanVO;
import com.yuedongyun.workout.enums.SessionStatus;
import com.yuedongyun.workout.enums.PlanStatus;
import com.yuedongyun.workout.mapper.WorkoutSessionMapper;
import com.yuedongyun.workout.mapper.WorkoutRecordMapper;
import com.yuedongyun.workout.service.IWorkoutSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 会员训练计划 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2022-12-02
 */
@SuppressWarnings("ALL")
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutSessionServiceImpl extends ServiceImpl<WorkoutSessionMapper, WorkoutSession> implements IWorkoutSessionService {

    private final TrainingClient trainingClient;
    private final OutlineClient outlineClient;
    private final WorkoutRecordMapper recordMapper;

    @Override
    @Transactional
    public void addUserSessions(Long userId, List<Long> trainingIds) {
        // 1.查询训练有效期
        List<TrainingSimpleInfoDTO> cInfoList = trainingClient.getSimpleInfoList(trainingIds);
        if (CollUtils.isEmpty(cInfoList)) {
            // 训练不存在，无法添加
        log.error("训练信息不存在，无法加入训练表");
            return;
        }
        // 2.循环遍历，处理WorkoutSession数据
        List<WorkoutSession> list = new ArrayList<>(cInfoList.size());
        for (TrainingSimpleInfoDTO cInfo : cInfoList) {
            WorkoutSession session = new WorkoutSession();
            // 2.1.获取过期时间
            Integer validDuration = cInfo.getValidDuration();
            if (validDuration != null && validDuration > 0) {
                LocalDateTime now = LocalDateTime.now();
                session.setCreateTime(now);
                session.setExpireTime(now.plusMonths(validDuration));
            }
            // 2.2.填充userId和trainingId
            session.setUserId(userId);
            session.setTrainingId(cInfo.getId());
            list.add(session);
        }
        // 3.批量新增
        saveBatch(list);
    }

    @Override
    public PageDTO<WorkoutSessionVO> queryMySessions(PageQuery query) {
        // 1.获取当前登录用户
        Long userId = UserContext.getUser();
        // 2.分页查询
        // select * from workout_session where user_id = #{userId} order by latest_learn_time limit 0, 5
        Page<WorkoutSession> page = lambdaQuery()
                .eq(WorkoutSession::getUserId, userId) // where user_id = #{userId}
                .page(query.toMpPage("latest_learn_time", false));
        List<WorkoutSession> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }
        // 3.查询训练信息
        Map<Long, TrainingSimpleInfoDTO> cMap = queryTrainingSimpleInfoList(records);

        // 4.封装VO返回
        List<WorkoutSessionVO> list = new ArrayList<>(records.size());
        // 4.1.循环遍历，把WorkoutSession转为VO
        for (WorkoutSession r : records) {
            // 4.2.拷贝基础属性到vo
            WorkoutSessionVO vo = BeanUtils.copyBean(r, WorkoutSessionVO.class);
            // 4.3.获取训练信息，填充到vo
            TrainingSimpleInfoDTO cInfo = cMap.get(r.getTrainingId());
            vo.setTrainingName(cInfo.getName());
            vo.setTrainingCoverUrl(cInfo.getCoverUrl());
            vo.setSessions(cInfo.getSessionNum());
            list.add(vo);
        }
        return PageDTO.of(page, list);
    }

    private Map<Long, TrainingSimpleInfoDTO> queryTrainingSimpleInfoList(List<WorkoutSession> records) {
        // 3.1.获取训练id
        Set<Long> cIds = records.stream().map(WorkoutSession::getTrainingId).collect(Collectors.toSet());
        // 3.2.查询训练信息
        List<TrainingSimpleInfoDTO> cInfoList = trainingClient.getSimpleInfoList(cIds);
        if (CollUtils.isEmpty(cInfoList)) {
            // 训练不存在，无法添加
            throw new BadRequestException("训练信息不存在！");
        }
        // 3.3.把训练集合处理成Map，key是trainingId，值是training本身
        Map<Long, TrainingSimpleInfoDTO> cMap = cInfoList.stream()
                .collect(Collectors.toMap(TrainingSimpleInfoDTO::getId, c -> c));
        return cMap;
    }

    @Override
    public WorkoutSessionVO queryMyCurrentSession() {
        // 1.获取当前登录的用户
        Long userId = UserContext.getUser();
        // 2.查询正在跟练的训练 select * from xx where user_id = #{userId} AND status = 1 order by latest_learn_time limit 1
        WorkoutSession session = lambdaQuery()
                .eq(WorkoutSession::getUserId, userId)
                .eq(WorkoutSession::getStatus, SessionStatus.WORKOUT.getValue())
                .orderByDesc(WorkoutSession::getLatestLearnTime)
                .last("limit 1")
                .one();
        if (session == null) {
            return null;
        }
        // 3.拷贝PO基础属性到VO
        WorkoutSessionVO vo = BeanUtils.copyBean(session, WorkoutSessionVO.class);
        // 4.查询训练信息
        TrainingFullInfoDTO cInfo = trainingClient.getTrainingInfoById(session.getTrainingId(), false, false);
        if (cInfo == null) {
            throw new BadRequestException("训练不存在");
        }
        vo.setTrainingName(cInfo.getName());
        vo.setTrainingCoverUrl(cInfo.getCoverUrl());
        vo.setSessions(cInfo.getSessionNum());
        // 5.统计课表中的训练数量 select count(1) from xxx where user_id = #{userId}
        Integer trainingAmount = lambdaQuery()
                .eq(WorkoutSession::getUserId, userId)
                .count();
        vo.setTrainingAmount(trainingAmount);
        // 6.查询小节信息
        List<OutlineSimpleInfoDTO> outlineInfos =
                outlineClient.batchQueryOutline(CollUtils.singletonList(session.getLatestSessionId()));
        if (!CollUtils.isEmpty(outlineInfos)) {
            OutlineSimpleInfoDTO outlineInfo = outlineInfos.get(0);
            vo.setLatestSessionName(outlineInfo.getName());
            vo.setLatestSessionIndex(outlineInfo.getCIndex());
        }
        return vo;
    }

    @Override
    public WorkoutSessionVO querySessionByTrainingId(Long trainingId) {
        // 1.获取当前登录用户
        Long userId = UserContext.getUser();
        // 2.查询训练信息 select * from xx where user_id = #{userId} AND training_id = #{trainingId}
        WorkoutSession session = getOne(buildUserIdAndTrainingIdWrapper(userId, trainingId));
        if (session == null) {
            return null;
        }
        // 3.处理VO
        return BeanUtils.copyBean(session, WorkoutSessionVO.class);
    }

    @Override
    public void deleteTrainingFromSession(Long userId, Long trainingId) {
        // 1.获取当前登录用户
        if (userId == null) {
            userId = UserContext.getUser();
        }
        // 2.删除训练
        remove(buildUserIdAndTrainingIdWrapper(userId, trainingId));
    }

    @Override
    public Integer countWorkoutSessionByTraining(Long trainingId) {
        // select count(1) from xx where training_id = #{cc} AND status in (0, 1, 2)
        return lambdaQuery()
                .eq(WorkoutSession::getTrainingId, trainingId)
                .in(WorkoutSession::getStatus,
                        SessionStatus.NOT_BEGIN.getValue(),
                        SessionStatus.WORKOUT.getValue(),
                        SessionStatus.FINISHED.getValue())
                .count();
    }

    @Override
    public Long isSessionValid(Long trainingId) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();
        if (userId == null) {
            return null;
        }
        // 2.查询训练
        WorkoutSession session = getOne(buildUserIdAndTrainingIdWrapper(userId, trainingId));
        if (session == null) {
            return null;
        }
        return session.getId();
    }

    @Override
    public WorkoutSession queryByUserAndTrainingId(Long userId, Long trainingId) {
        return getOne(buildUserIdAndTrainingIdWrapper(userId, trainingId));
    }

    @Override
    public void createWorkoutPlan(Long trainingId, Integer freq) {
        // 1.获取当前登录的用户
        Long userId = UserContext.getUser();
        // 2.查询课表中的指定训练有关的数据
        WorkoutSession session = queryByUserAndTrainingId(userId, trainingId);
        AssertUtils.isNotNull(session, "训练信息不存在！");
        // 3.修改数据
        WorkoutSession l = new WorkoutSession();
        l.setId(session.getId());
        l.setWeekFreq(freq);
        if(session.getPlanStatus() == PlanStatus.NO_PLAN) {
            l.setPlanStatus(PlanStatus.PLAN_RUNNING);
        }
        updateById(l);
    }

    @Override
    public WorkoutPlanPageVO queryMyPlans(PageQuery query) {
        WorkoutPlanPageVO result = new WorkoutPlanPageVO();
        // 1.获取当前登录用户
        Long userId = UserContext.getUser();
        // 2.获取本周起始时间
        LocalDate now = LocalDate.now();
        LocalDateTime begin = DateUtils.getWeekBeginTime(now);
        LocalDateTime end = DateUtils.getWeekEndTime(now);
        // 3.查询总的统计数据
        // 3.1.本周总的已跟练小节数量
        Integer weekFinished = recordMapper.selectCount(new LambdaQueryWrapper<WorkoutRecord>()
                .eq(WorkoutRecord::getUserId, userId)
                .eq(WorkoutRecord::getFinished, true)
                .gt(WorkoutRecord::getFinishTime, begin)
                .lt(WorkoutRecord::getFinishTime, end)
        );
        result.setWeekFinished(weekFinished);
        // 3.2.本周总的计划跟练小节数量
        Integer weekTotalPlan = getBaseMapper().queryTotalPlan(userId);
        result.setWeekTotalPlan(weekTotalPlan);
        // TODO 3.3.本周跟练积分

        // 4.查询分页数据
        // 4.1.分页查询课表信息以及训练计划信息
        Page<WorkoutSession> p = lambdaQuery()
                .eq(WorkoutSession::getUserId, userId)
                .eq(WorkoutSession::getPlanStatus, PlanStatus.PLAN_RUNNING)
                .in(WorkoutSession::getStatus, SessionStatus.NOT_BEGIN, SessionStatus.WORKOUT)
                .page(query.toMpPage("latest_learn_time", false));
        List<WorkoutSession> records = p.getRecords();
        if (CollUtils.isEmpty(records)) {
            return result.emptyPage(p);
        }
        // 4.2.查询课表对应的训练信息
        Map<Long, TrainingSimpleInfoDTO> cMap = queryTrainingSimpleInfoList(records);
        // 4.3.统计每一个训练本周已跟练小节数量
        List<IdAndNumDTO> list = recordMapper.countLearnedSessions(userId, begin, end);
        Map<Long, Integer> countMap = IdAndNumDTO.toMap(list);
        // 4.4.组装数据VO
        List<WorkoutPlanVO> voList = new ArrayList<>(records.size());
        for (WorkoutSession r : records) {
            // 4.4.1.拷贝基础属性到vo
            WorkoutPlanVO vo = BeanUtils.copyBean(r, WorkoutPlanVO.class);
            // 4.4.2.填充训练详细信息
            TrainingSimpleInfoDTO cInfo = cMap.get(r.getTrainingId());
            if (cInfo != null) {
                vo.setTrainingName(cInfo.getName());
                vo.setSessions(cInfo.getSessionNum());
            }
            // 4.4.3.每个训练的本周已跟练小节数量
            vo.setWeekLearnedSessions(countMap.getOrDefault(r.getId(), 0));
            voList.add(vo);
        }
        return result.pageInfo(p.getTotal(), p.getPages(), voList);
    }

    private LambdaQueryWrapper<WorkoutSession> buildUserIdAndTrainingIdWrapper(Long userId, Long trainingId) {
        LambdaQueryWrapper<WorkoutSession> queryWrapper = new QueryWrapper<WorkoutSession>()
                .lambda()
                .eq(WorkoutSession::getUserId, userId)
                .eq(WorkoutSession::getTrainingId, trainingId);
        return queryWrapper;
    }
}

