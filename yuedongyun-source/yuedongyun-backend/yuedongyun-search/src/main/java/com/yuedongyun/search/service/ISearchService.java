package com.yuedongyun.search.service;

import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.search.domain.query.TrainingPageQuery;
import com.yuedongyun.search.domain.vo.TrainingVO;

import java.util.List;

public interface ISearchService {

    List<TrainingVO> queryTrainingByCateId(Long cateLv2Id);

    List<TrainingVO> queryBestTopN();

    List<TrainingVO> queryNewTopN();

    List<TrainingVO> queryFreeTopN();

    PageDTO<TrainingVO> queryTrainingsForPortal(TrainingPageQuery query);

    List<Long> queryTrainingsIdByName(String keyword);
}

