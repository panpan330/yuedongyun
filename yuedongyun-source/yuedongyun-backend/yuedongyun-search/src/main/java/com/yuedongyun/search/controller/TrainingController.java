package com.yuedongyun.search.controller;

import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.search.domain.query.TrainingPageQuery;
import com.yuedongyun.search.domain.vo.TrainingVO;
import com.yuedongyun.search.service.ITrainingService;
import com.yuedongyun.search.service.ISearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("trainings")
@Api(tags = "训练搜索接口")
@RequiredArgsConstructor
public class TrainingController {

    private final ISearchService searchService;
    private final ITrainingService trainingService;

    @ApiOperation("用户端训练搜索接口")
    @GetMapping("/portal")
    public PageDTO<TrainingVO> queryTrainingsForPortal(TrainingPageQuery query){
        return searchService.queryTrainingsForPortal(query);
    }

    @ApiIgnore
    @GetMapping("/name")
    public List<Long> queryTrainingsIdByName(@RequestParam("keyword") String keyword){
        return searchService.queryTrainingsIdByName(keyword);
    }

    @ApiOperation("处理指定训练上架失败的问题")
    @PostMapping("/up")
    public void handleTrainingsUp(
            @ApiParam("训练id集合") @RequestParam("trainingIds") List<Long> trainingIds) {
        for (Long trainingId : trainingIds) {
            trainingService.handleTrainingUp(trainingId);
        }
    }

    @ApiOperation("处理指定训练下架失败的问题")
    @PostMapping("/down")
    public void handleTrainingsDown(
            @ApiParam("训练id集合") @RequestParam("trainingIds") List<Long> trainingIds) {
        for (Long trainingId : trainingIds) {
            trainingService.handleTrainingDeletes(trainingIds);
        }
    }
}

