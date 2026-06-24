package com.yuedongyun.training.service;

import com.yuedongyun.training.domain.dto.TrainingCoachSaveDTO;
import com.yuedongyun.training.domain.po.TrainingCoachDraft;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.training.domain.vo.TrainingCoachVO;

import java.util.List;

/**
 * <p>
 * 训练教练关系表草稿 服务类
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
public interface ITrainingCoachDraftService extends IService<TrainingCoachDraft> {

    /**
     * 保存训练指定的教练
     * @param trainingCoachSaveDTO 教练数据
     */
    void save(TrainingCoachSaveDTO trainingCoachSaveDTO);

    /**
     * 查询指定训练对应的教练
     *
     * @param trainingId 训练id
     * @param see 是否用于查看
     * @return 教练数据
     */
    List<TrainingCoachVO> queryCoachOfTraining(Long trainingId,Boolean see);

    /**
     * 训练教练上架
     * @param trainingId 训练id
     */
    void copyToShelf(Long trainingId, Boolean isFirstShelf);

}

