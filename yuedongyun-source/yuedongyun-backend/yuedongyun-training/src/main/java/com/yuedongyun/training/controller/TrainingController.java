package com.yuedongyun.training.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.yuedongyun.api.dto.training.TrainingSimpleInfoDTO;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.common.validate.annotations.ParamChecker;
import com.yuedongyun.training.constants.TrainingStatus;
import com.yuedongyun.training.domain.dto.*;
import com.yuedongyun.training.domain.vo.*;
import com.yuedongyun.training.service.*;
import com.yuedongyun.training.utils.TrainingSaveBaseGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 训练controller
 *
 * @ClassName TrainingController
 * @Author wusongsong
 * @Date 2022/7/10 15:34
 * @Version
 **/
@Api(tags = "训练相关接口")
@RestController
@RequestMapping("trainings")
@Slf4j
@Validated
public class TrainingController {

    @Autowired
    private ITrainingDraftService trainingDraftService;

    @Autowired
    private ITrainingOutlineDraftService trainingOutlineDraftService;

    @Autowired
    private ITrainingCoachDraftService trainingCoachDraftService;

    @Autowired
    private ITrainingService trainingService;

    @Autowired
    private ITrainingOutlineService trainingOutlineService;

    //todo 二期做
//    @GetMapping("statistics")
//    @ApiOperation("训练数据统计")
    public TrainingStatisticsVO statistics() {
        return null;
    }

    @GetMapping("baseInfo/{id}")
    @ApiOperation("获取训练基础信息")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "训练id"),
            @ApiImplicitParam(name = "see", value = "是否是用于查看页面查看数据，默认是查看,如果不是界面查看数据就是编辑页面使用")})
    public TrainingBaseInfoVO baseInfo(@PathVariable("id") Long id,
                                     @RequestParam(value = "see", required = false, defaultValue = "1") Boolean see) {
        return trainingDraftService.getTrainingBaseInfo(id, see);
    }

    @PostMapping("baseInfo/save")
    @ApiOperation("保存训练基本信息")
    @ParamChecker
    //校验非业务限制的字段
    public TrainingSaveVO save(@RequestBody @Validated(TrainingSaveBaseGroup.class) TrainingBaseInfoSaveDTO trainingBaseInfoSaveDTO) {
        return trainingDraftService.save(trainingBaseInfoSaveDTO);
    }

    @GetMapping("outlines/{id}")
    @ApiOperation("获取训练目录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "训练id"),
            @ApiImplicitParam(name = "see", value = "是否是用于查看页面查看数据，默认是查看,如果不是界面查看数据就是编辑页面使用")
    })
    public List<OutlineVO> outlines(@PathVariable(value = "id", required = false) Long id,
                              @RequestParam(value = "see", required = false, defaultValue = "1") Boolean see,
                              @RequestParam(value = "withPractice", required = false, defaultValue = "1") Boolean withPractice) {
        return trainingOutlineDraftService.queryTrainingOutlines(id, see, withPractice);
    }

    @PostMapping("outlines/save/{id}/{step}")
    @ApiOperation("保存训练目录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "训练id"),
            @ApiImplicitParam(name = "step", value = "步骤")
    })
    @ParamChecker
    public void outlinesSave(@RequestBody @Validated List<OutlineSaveDTO> outlineSaveDTOS,
                          @PathVariable("id") Long id, @PathVariable(value = "step",required = false) Integer step) {
        trainingOutlineDraftService.save(id, outlineSaveDTOS, step);
    }

    @PostMapping("media/save/{id}")
    @ApiOperation("训练视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "训练id")
    })
    public void mediaSave(@PathVariable("id") Long id, @RequestBody @Valid List<TrainingMediaDTO> trainingMediaDTOS) {
        trainingOutlineDraftService.saveMediaInfo(id, trainingMediaDTOS);
    }

    @PostMapping("assessments/save/{id}")
    @ApiOperation("保存小节或练习中的题目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "训练id")
    })
    public void saveSuject(@PathVariable("id") Long id, @RequestBody @Validated List<OutlineAssessmentDTO> outlineAssessmentDTO) {
        trainingOutlineDraftService.saveSuject(id, outlineAssessmentDTO);
    }

    @GetMapping("assessments/get/{id}")
    @ApiOperation("获取小节或练习中的题目（用于编辑）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "训练id")
    })
    public List<OutlineSimpleAssessmentVO> getSuject(@PathVariable("id") Long id) {
        return trainingOutlineDraftService.getSuject(id);
    }

    @GetMapping("coaches/{id}")
    @ApiOperation("查询训练相关的教练信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "训练id"),
            @ApiImplicitParam(name = "see", value = "是否是用于查看页面查看数据，默认是查看,如果不是界面查看数据就是编辑页面使用")
    })
    public List<TrainingCoachVO> coach(@PathVariable("id") Long id,
                                         @RequestParam(value = "see", required = false, defaultValue = "1") Boolean see) {
        return trainingCoachDraftService.queryCoachOfTraining(id, see);
    }

    @PostMapping("coaches/save")
    @ApiOperation("保存教练信息")
    public void coachsSave(@RequestBody @Validated TrainingCoachSaveDTO trainingCoachSaveDTO) {
        trainingCoachDraftService.save(trainingCoachSaveDTO);
    }


    @PostMapping("upShelf")
    @ApiOperation("训练上架")
    public void upShelf(@RequestBody @Validated TrainingIdDTO trainingIdDTO) {
        trainingDraftService.upShelf(trainingIdDTO.getId());
    }

    @GetMapping("checkBeforeUpShelf/{id}")
    @ApiOperation("训练上架前校验")
    public void checkBeforeUpShelf(@PathVariable("id") Long id){
        trainingDraftService.checkBeforeUpShelf(id);
    }

    @PostMapping("downShelf")
    @ApiOperation("训练下架")
    public void downShelf(@RequestBody @Validated TrainingIdDTO trainingIdDTO) {
        trainingDraftService.downShelf(trainingIdDTO.getId());
    }

    /**
     * 先去删除加上数据删除后，再去删除草稿
     *
     * @param id
     */
    @DeleteMapping("delete/{id}")
    @ApiOperation("训练删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id")
    })
    public void deleteById(@PathVariable("id") Long id) {
        trainingService.delete(id);
    }

    @ApiOperation("根据条件列表获取训练信息")
    @GetMapping("/simpleInfo/list")
    public List<TrainingSimpleInfoDTO> getSimpleInfoList(TrainingSimpleInfoListDTO trainingSimpleInfoListDTO) {
        return trainingService.getSimpleInfoList(trainingSimpleInfoListDTO);
    }

    @ApiOperation("根据训练id，查询所有章节的序号")
    @GetMapping("/outlines/index/list/{id}")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "训练id")
    )
    public List<OutlineSimpleInfoVO> outlinesIndexList(@PathVariable("id") Long id) {
        return trainingOutlineService.getOutlinesIndexList(id);
    }

    @ApiOperation("生成练习id")
    @GetMapping("generator")
    public TrainingOutlineIdVO generator() {
        return new TrainingOutlineIdVO(IdWorker.getId());
    }

    @ApiOperation("管理端训练搜索接口")
    @GetMapping("/page")
    public PageDTO<TrainingPageVO> queryForPage(TrainingPageQuery trainingPageQuery) {
        if(TrainingStatus.NO_UP_SHELF.equals(trainingPageQuery.getStatus()) ||
        TrainingStatus.DOWN_SHELF.equals(trainingPageQuery.getStatus())){
            //待上架已下架查询草稿
            return trainingDraftService.queryForPage(trainingPageQuery);
        }else {
            //已上架已完结查询正式数据
            return trainingService.queryForPage(trainingPageQuery);
        }
    }

    @ApiOperation("校验训练名称是否已经存在")
    @GetMapping("/checkName")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id"),
            @ApiImplicitParam(name = "name", value = "训练名称")
    })
    public NameExistVO checkNameExist(@RequestParam(value = "id",required = false) Long id,
                                      @RequestParam(value = "name") String name){
        return trainingService.checkName(name, id);
    }

    @ApiOperation("查询训练基本信息、目录、跟练进度")
    @GetMapping("/{id}/outlinelogs")
    public TrainingAndSessionVO queryTrainingAndOutlinelogById(@PathVariable("id") Long trainingId){
        return trainingService.queryTrainingAndOutlinelogById(trainingId);
    }
}

