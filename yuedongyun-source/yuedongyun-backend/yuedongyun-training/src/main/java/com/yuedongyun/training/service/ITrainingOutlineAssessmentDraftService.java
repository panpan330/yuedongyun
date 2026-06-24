package com.yuedongyun.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.training.domain.po.TrainingOutlineAssessmentDraft;

/**
 * <p>
 * 训练-题目关系表草稿 服务类
 * </p>
 *
 * @author wusongsong
 * @since 2022-09-21
 */
public interface ITrainingOutlineAssessmentDraftService extends IService<TrainingOutlineAssessmentDraft> {
    /**
     * 删除不在的训练小节目录
     * @param trainingId
     */
    void deleteNotInOutlineIdList(Long trainingId);
}

