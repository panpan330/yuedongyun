package com.yuedongyun.training.controller;

import com.yuedongyun.api.dto.training.*;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.training.service.ICategoryService;
import com.yuedongyun.training.service.ITrainingOutlineService;
import com.yuedongyun.training.service.ITrainingService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 内部服务调用接口
 */
@RestController
@RequestMapping("training")
@Api(tags = "训练相关接口，内部调用")
public class TrainingInfoController {

    @Autowired
    private ITrainingOutlineService trainingOutlineService;

    @Autowired
    private ITrainingService trainingService;

    @Autowired
    private ICategoryService categoryService;

    @GetMapping("infoByCoachIds")
    @ApiOperation("通过教练id获取教练负责的训练数量和题目数量")
    public List<SubNumAndTrainingNumDTO> infoByCoachIds(@RequestParam("coachIds") List<Long> coachIds) {
        if (CollUtils.isEmpty(coachIds)) {
            return new ArrayList<>();
        }
        return trainingService.countAssessmentNumAndTrainingNumOfCoach(coachIds);
    }

    /**
     * 根据小节id获取小节对应的mediaId和训练id
     */
    @GetMapping("/session/{id}")
    @ApiImplicitParam(name = "id", value = "小节id，不支持章id或练习id查询")
    public SessionInfoDTO sessionInfo(@PathVariable("id") Long sessionId) {
        return trainingOutlineService.getSimpleSessionInfo(sessionId);
    }

    /**
     * 根据媒资Id列表查询媒资被引用的次数
     */
    @GetMapping("/media/useInfo")
    public List<MediaQuoteDTO> mediaUserInfo(@RequestParam("mediaIds") List<Long> mediaIds) {
        return trainingOutlineService.countMediaUserInfo(mediaIds);
    }

    @GetMapping("/{id}/searchInfo")
    @ApiOperation("训练上架时查询训练信息并写入索引库")
    public TrainingDTO getSearchInfo(@ApiParam("训练id") @PathVariable("id") Long id) {
        return trainingService.getTrainingDTOById(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取训练信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "训练id"),
            @ApiImplicitParam(name = "withOutline", value = "是否查询目录信息"),
            @ApiImplicitParam(name = "withCoaches", value = "是否查询教练信息")
    })
    public TrainingFullInfoDTO getById(
            @PathVariable("id") Long id,
            @RequestParam(value = "withOutline", required = false) boolean withOutline,
            @RequestParam(value = "withCoaches", required = false) boolean withCoaches) {
        return trainingService.getInfoById(id, withOutline, withCoaches);
    }

    @GetMapping("/getCateNameMap")
    @ApiIgnore
    public Map<Long, String> queryByThirdCateIds(@RequestParam("thirdCateIdList") List<Long> thirdCateIdList) {
        return categoryService.queryByThirdCateIds(thirdCateIdList);
    }

    @GetMapping("/name")
    public List<Long> queryTrainingsIdByName(@RequestParam("name") String name) {
        return trainingService.queryTrainingIdByName(name);
    }
}

