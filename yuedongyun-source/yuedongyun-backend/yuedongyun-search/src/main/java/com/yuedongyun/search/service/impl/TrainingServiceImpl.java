package com.yuedongyun.search.service.impl;

import com.yuedongyun.api.client.training.TrainingClient;
import com.yuedongyun.api.dto.training.TrainingSearchDTO;
import com.yuedongyun.common.utils.BeanUtils;
import com.yuedongyun.search.domain.po.Training;
import com.yuedongyun.search.repository.TrainingRepository;
import com.yuedongyun.search.service.ITrainingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TrainingServiceImpl implements ITrainingService {

    @Resource
    private TrainingRepository trainingRepository;
    @Resource
    private TrainingClient trainingClient;

    @Override
    public void handleTrainingDelete(Long trainingId) {
        // 1.直接删除
        trainingRepository.deleteById(trainingId);
    }

    @Override
    public void handleTrainingUp(Long trainingId) {
        // 1.根据id查询训练信息
        TrainingSearchDTO trainingSearchDTO = trainingClient.getSearchInfo(trainingId);
        if (trainingSearchDTO == null) {
            return;
        }
        // 2.数据转换
        Training training = BeanUtils.toBean(trainingSearchDTO, Training.class);
        training.setType(trainingSearchDTO.getTrainingType());
        // 3.写入索引库
        trainingRepository.save(training);

    }

    @Override
    public void updateTrainingSold(List<Long> trainingIds, int amount) {
        trainingRepository.incrementSold(trainingIds, amount);
    }

    @Override
    public void handleTrainingDeletes(List<Long> trainingIds) {
        // 1.直接删除
        trainingRepository.deleteByIds(trainingIds);
    }
}

