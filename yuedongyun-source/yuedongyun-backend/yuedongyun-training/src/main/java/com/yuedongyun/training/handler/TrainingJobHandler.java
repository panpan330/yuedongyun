package com.yuedongyun.training.handler;

import com.yuedongyun.training.service.ITrainingService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName TrainingJobHandler
 * @Author wusongsong
 * @Date 2022/9/16 15:04
 * @Version
 **/
@Component
@Slf4j
public class TrainingJobHandler {

    @Autowired
    private ITrainingService trainingService;

    @XxlJob("trainingFinished")
    public void trainingFinished(){
        trainingService.trainingFinished();
    }
}

