package com.yuedongyun.search.controller;

import com.yuedongyun.search.domain.vo.TrainingVO;
import com.yuedongyun.search.service.ISearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "训练推荐相关接口")
@RequiredArgsConstructor
@RestController
@RequestMapping("recommend")
public class RecommendController {

    private final ISearchService searchService;

    @ApiOperation("精品好课接口")
    @GetMapping("/best")
    public List<TrainingVO> queryBestTopN(){
        return searchService.queryBestTopN();
    }

    @ApiOperation("新课推荐接口")
    @GetMapping("/new")
    public List<TrainingVO> queryNewTopN(){
        return searchService.queryNewTopN();
    }

    @ApiOperation("精品公开课接口")
    @GetMapping("/free")
    public List<TrainingVO> queryFreeTopN(){
        return searchService.queryFreeTopN();
    }

}

