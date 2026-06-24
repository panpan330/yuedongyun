package com.yuedongyun.workout.service;

import com.yuedongyun.workout.domain.vo.SignResultVO;

public interface ISignRecordService {
    SignResultVO addSignRecords();

    Byte[] querySignRecords();

}

