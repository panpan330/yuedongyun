package com.yuedongyun.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.api.client.user.UserClient;
import com.yuedongyun.api.dto.user.UserDTO;
import com.yuedongyun.common.constants.ErrorInfo;
import com.yuedongyun.common.exceptions.DbException;
import com.yuedongyun.common.utils.BeanUtils;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.training.constants.TrainingConstants;
import com.yuedongyun.training.domain.dto.TrainingCoachSaveDTO;
import com.yuedongyun.training.domain.po.TrainingCoach;
import com.yuedongyun.training.domain.po.TrainingCoachDraft;
import com.yuedongyun.training.domain.vo.TrainingCoachVO;
import com.yuedongyun.training.mapper.TrainingCoachDraftMapper;
import com.yuedongyun.training.service.ITrainingDraftService;
import com.yuedongyun.training.service.ITrainingCoachDraftService;
import com.yuedongyun.training.service.ITrainingCoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 训练教练关系表草稿 服务实现类
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
@Service
public class TrainingCoachDraftServiceImpl extends ServiceImpl<TrainingCoachDraftMapper, TrainingCoachDraft> implements ITrainingCoachDraftService {

    @Autowired
    private ITrainingDraftService trainingDraftService;

    @Autowired
    private ITrainingCoachService trainingCoachService;

    @Autowired
    private UserClient userClient;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(TrainingCoachSaveDTO trainingCoachSaveDTO) {

        //1.数据删除条件
        LambdaUpdateWrapper<TrainingCoachDraft> updateWrapper =
                Wrappers.lambdaUpdate(TrainingCoachDraft.class)
                        .eq(TrainingCoachDraft::getTrainingId, trainingCoachSaveDTO.getId());
        //1.1.数据删除
        baseMapper.delete(updateWrapper);

        //2.组装即将插入的数据
        List<TrainingCoachDraft> trainingCoachDrafts =
                BeanUtils.copyList(trainingCoachSaveDTO.getCoaches(),
                        TrainingCoachDraft.class, (coachInfo, coachDraft) -> {
                            //2.1.设置训练id
                            coachDraft.setTrainingId(trainingCoachSaveDTO.getId());
                            //2.2.设置教练id
                            coachDraft.setCoachId(coachInfo.getId());
                            //2.3.设置训练中教练排序
                            coachDraft.setCIndex(trainingCoachSaveDTO.getCoaches().indexOf(coachInfo));
                        });
        //3.批量插入训练的教练信息
        saveBatch(trainingCoachDrafts);
        //4.更新训练填写进度
        trainingDraftService.updateStep(trainingCoachSaveDTO.getId(), TrainingConstants.TrainingStep.COACH);
    }

    @Override
    public List<TrainingCoachVO> queryCoachOfTraining(Long trainingId, Boolean see) {
        if (see) {
            //1.查询训练教练关系
            List<TrainingCoachVO> trainingCoachVOS = trainingCoachService.queryCoachs(trainingId);
            //1.1.训练教练关系判非空
            if (CollUtils.isNotEmpty(trainingCoachVOS)) {
                return trainingCoachVOS;
            }
            //2.查询草稿中的训练教练关系
            trainingCoachVOS = queryCoachs(trainingId);
            //3.组装数据
            return CollUtils.isEmpty(trainingCoachVOS) ? new ArrayList<>() : trainingCoachVOS;
        } else {
            //4.查询草稿中的训练教练关系
            return queryCoachs(trainingId);
        }
    }

    @Override
    @Transactional(rollbackFor = {DbException.class, Exception.class})
    public void copyToShelf(Long trainingId, Boolean isFirstShelf) {
        //1.先将草稿中的数据查出来
        LambdaQueryWrapper<TrainingCoachDraft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrainingCoachDraft::getTrainingId, trainingId);
        List<TrainingCoachDraft> trainingCoachDrafts = baseMapper.selectList(queryWrapper);
        //2.删除架上的教练数据，
        if (!isFirstShelf) {
            trainingCoachService.deleteByTrainingId(trainingId);
        }
        //3.将草稿上架
        List<TrainingCoach> trainingCoachs = BeanUtils.copyList(trainingCoachDrafts, TrainingCoach.class);
        trainingCoachService.saveOrUpdateBatch(trainingCoachs);
        //4.删除草稿
        if (baseMapper.deleteByTrainingId(trainingId) <= 0) {
            throw new DbException(ErrorInfo.Msg.DB_DELETE_EXCEPTION);
        }
    }

    private List<TrainingCoachVO> queryCoachs(Long couserId) {

        //1.查询条件
        LambdaQueryWrapper<TrainingCoachDraft> queryWrapper =
                Wrappers.lambdaQuery(TrainingCoachDraft.class)
                        .eq(TrainingCoachDraft::getTrainingId, couserId);
        //1.1.查询数据
        List<TrainingCoachDraft> trainingCoachDrafts = baseMapper.selectList(queryWrapper);
        //1.2.数据判空
        if (CollUtils.isEmpty(trainingCoachDrafts)) {
            return new ArrayList<>();
        }

        // 2.查询教练详细信息
        List<UserDTO> UserDTOS = userClient.queryUserByIds(
                trainingCoachDrafts.stream().map(TrainingCoachDraft::getCoachId).collect(Collectors.toList()));
        // 3.组织为map
        Map<Long, UserDTO> UserDTOMap = UserDTOS.stream().collect(Collectors.toMap(UserDTO::getId, UserDTO -> UserDTO));

        //4.数据组装
        return BeanUtils.copyList(trainingCoachDrafts, TrainingCoachVO.class,
                (trainingCoach, trainingCoachVO) -> {
            //4.1.教练信息
                    UserDTO coachDetailDTO = UserDTOMap.get(trainingCoach.getCoachId());
            if (coachDetailDTO != null) {
                //4.2.设置教练形象照
                trainingCoachVO.setIcon(coachDetailDTO.getIcon());
                trainingCoachVO.setPhoto(coachDetailDTO.getPhoto());
                //4.3.设置教练姓名
                trainingCoachVO.setName(coachDetailDTO.getName());
                //4.4.设置教练介绍
                trainingCoachVO.setIntroduce(coachDetailDTO.getIntro());
                //4.5.设置教练职业
                trainingCoachVO.setJob(coachDetailDTO.getJob());
            }
            //4.6.设置教练id
            trainingCoachVO.setId(trainingCoach.getCoachId());
        });
    }

}

