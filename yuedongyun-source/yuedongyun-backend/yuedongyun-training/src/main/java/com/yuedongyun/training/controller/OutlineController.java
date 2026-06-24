package com.yuedongyun.training.controller;

import com.yuedongyun.training.domain.vo.OutlineSimpleInfoVO;
import com.yuedongyun.training.service.ITrainingOutlineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 目录训练相关接口
 *
 * @ClassName OutlineController
 * @Author wusongsong
 * @Date 2022/7/27 13:59
 * @Version
 **/
@Api(tags = "章节目录相关接口")
@RestController
@RequestMapping("outlines")
public class OutlineController {

    @Autowired
    private ITrainingOutlineService trainingOutlineService;

    @GetMapping("batchQuery")
    @ApiOperation("根据章节目录批量查询基础信息")
    public List<OutlineSimpleInfoVO> batchQuery(@RequestParam("ids") List<Long> ids) {
        return trainingOutlineService.getManyOutlineSimpleInfo(ids);
    }

    @GetMapping("querySessionInfoById/{id}")
    @ApiOperation("获取小节信息")
    public OutlineSimpleInfoVO querySessionInfoById(@PathVariable("id") Long id) {
        return trainingOutlineService.querySessionInfoById(id);
    }
}

