package com.yuedongyun.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.training.domain.po.TrainingCoach;
import com.yuedongyun.training.domain.vo.TrainingCoachVO;

import java.util.List;

/**
 * <p>
 * 训练教练关系表 服务类
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
public interface ITrainingCoachService extends IService<TrainingCoach> {

    /**
     * 查询教练训练信息
     * @param couserId 训练id
     * @return 教练信息
     */
    List<TrainingCoachVO> queryCoachs(Long couserId);

    void deleteByTrainingId(Long trainingrId);

    /**
     * 根据训练id获取教练id列表，并且排序
     * @param trainingId 训练id
     * @return 教练信息
     */
    List<Long> getCoachIdOfTraining(Long trainingId);


}

