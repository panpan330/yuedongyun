package com.yuedongyun.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.training.constants.TrainingConstants;
import com.yuedongyun.training.domain.po.TrainingOutlineAssessmentDraft;
import com.yuedongyun.training.mapper.TrainingOutlineAssessmentDraftMapper;
import com.yuedongyun.training.service.ITrainingOutlineAssessmentDraftService;
import com.yuedongyun.training.service.ITrainingOutlineDraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 训练-题目关系表草稿 服务实现类
 * </p>
 *
 * @author wusongsong
 * @since 2022-09-21
 */
@Service
public class TrainingOutlineAssessmentDraftServiceImpl extends ServiceImpl<TrainingOutlineAssessmentDraftMapper, TrainingOutlineAssessmentDraft> implements ITrainingOutlineAssessmentDraftService {

    @Autowired
    private ITrainingOutlineDraftService trainingOutlineDraftService;

    @Override
    @Transactional
    public void deleteNotInOutlineIdList(Long trainingId) {

        //1.获取所有训练的小节和练习
        List<Long> outlineIdList = trainingOutlineDraftService.queryOutlineIdsOfTraining(trainingId,
                Arrays.asList(
                        TrainingConstants.OutlineType.SESSION,
                        TrainingConstants.OutlineType.PRATICE
                ));
        //1.删除条件
        LambdaUpdateWrapper<TrainingOutlineAssessmentDraft> updateWrapper =
                Wrappers.lambdaUpdate(TrainingOutlineAssessmentDraft.class)
                        .eq(TrainingOutlineAssessmentDraft::getTrainingId, trainingId)
                        .notIn(CollUtils.isNotEmpty(outlineIdList),
                                TrainingOutlineAssessmentDraft::getOutlineId, outlineIdList);
        //2.删除题目
        baseMapper.delete(updateWrapper);
    }
}

