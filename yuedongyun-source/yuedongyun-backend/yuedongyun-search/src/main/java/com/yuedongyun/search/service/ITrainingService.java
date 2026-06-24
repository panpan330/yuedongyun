package com.yuedongyun.search.service;

import java.util.List;

public interface ITrainingService {

    void handleTrainingDelete(Long trainingId);

    void handleTrainingUp(Long trainingId);

    void updateTrainingSold(List<Long> trainingId, int amount);

    void handleTrainingDeletes(List<Long> trainingIds);
}

