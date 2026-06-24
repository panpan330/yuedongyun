package com.yuedongyun.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.api.client.exam.ExamClient;
import com.yuedongyun.api.client.workout.WorkoutClient;
import com.yuedongyun.api.client.trade.TradeClient;
import com.yuedongyun.api.client.user.UserClient;
import com.yuedongyun.api.dto.training.TrainingDTO;
import com.yuedongyun.api.dto.training.TrainingPurchaseInfoDTO;
import com.yuedongyun.api.dto.exam.QuestionBizDTO;
import com.yuedongyun.api.dto.user.UserDTO;
import com.yuedongyun.common.autoconfigure.mq.RabbitMqHelper;
import com.yuedongyun.common.constants.ErrorInfo;
import com.yuedongyun.common.constants.MqConstants;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.common.exceptions.BadRequestException;
import com.yuedongyun.common.exceptions.BizIllegalException;
import com.yuedongyun.common.exceptions.DbException;
import com.yuedongyun.common.utils.*;
import com.yuedongyun.training.constants.TrainingConstants;
import com.yuedongyun.training.constants.TrainingErrorInfo;
import com.yuedongyun.training.constants.TrainingStatus;
import com.yuedongyun.training.domain.dto.TrainingBaseInfoSaveDTO;
import com.yuedongyun.training.domain.dto.TrainingPageQuery;
import com.yuedongyun.training.domain.po.*;
import com.yuedongyun.training.domain.vo.TrainingBaseInfoVO;
import com.yuedongyun.training.domain.vo.TrainingPageVO;
import com.yuedongyun.training.domain.vo.TrainingSaveVO;
import com.yuedongyun.training.domain.vo.NameExistVO;
import com.yuedongyun.training.mapper.*;
import com.yuedongyun.training.service.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 草稿训练 服务实现类
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-18
 */
@Service
public class TrainingDraftServiceImpl extends ServiceImpl<TrainingDraftMapper, TrainingDraft> implements ITrainingDraftService {

    @Autowired
    private TrainingMapper trainingMapper;

    @Autowired
    private ITrainingService trainingService;

    @Autowired
    private TrainingContentDraftMapper trainingContentDraftMapper;

    @Autowired
    private TrainingContentMapper trainingContentMapper;

    @Autowired
    private ValidatorFactory validatorFactory;

    @Autowired
    private ITrainingOutlineDraftService trainingOutlineDraftService;

    @Autowired
    private ITrainingCoachDraftService trainingCoachDraftService;

    @Autowired
    private TrainingOutlineDraftMapper trainingOutlineDraftMapper;

    @Autowired
    private TrainingCoachDraftMapper trainingCoachDraftMapper;

    @Autowired
    private TrainingOutlineAssessmentDraftMapper trainingOutlineAssessmentDraftMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private RabbitMqHelper rabbitMqHelper;

    @Autowired
    private TradeClient tradeClient;

    @Autowired
    private ExamClient examClient;

    @Autowired
    private WorkoutClient workoutClient;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public TrainingSaveVO save(TrainingBaseInfoSaveDTO trainingBaseInfoSaveDTO) {
        List<Long> categoryIdList = null;
        Training training = null;
        //1.数据校验
        if (trainingBaseInfoSaveDTO.getId() == null) {
            //1.1新增数据调起数据校验器
            ViolationUtils.process(validatorFactory.getValidator().validate(trainingBaseInfoSaveDTO));
            //1.1.2.校验训练分类
            categoryIdList = categoryService.checkCategory(trainingBaseInfoSaveDTO.getThirdCateId());
        } else {
            //1.2.未上架训练校验
            training = trainingMapper.selectById(trainingBaseInfoSaveDTO.getId());
            if (training == null) {
                //1.2.1.未上架训练校验请求参数
                ViolationUtils.process(validatorFactory.getValidator().validate(trainingBaseInfoSaveDTO));
                //1.2.2.同名训练判空
                checkSameTraining(trainingBaseInfoSaveDTO.getId(), trainingBaseInfoSaveDTO.getName());
                //1.2.3.校验训练分类
                categoryIdList = categoryService.checkCategory(trainingBaseInfoSaveDTO.getThirdCateId());
            }
        }

        TrainingDraft trainingDraft = new TrainingDraft();
        //2.数据封装
        //2.1.content数据封装 训练介绍、训练细节、适用人群
        TrainingContentDraft trainingContentDraft = new TrainingContentDraft();
        trainingContentDraft.setTrainingIntroduce(trainingBaseInfoSaveDTO.getIntroduce());
        trainingContentDraft.setTrainingDetail(trainingBaseInfoSaveDTO.getDetail());
        trainingContentDraft.setUsePeople(trainingBaseInfoSaveDTO.getUsePeople());
        //2.2.训练封面和训练下架时间
        trainingDraft.setCoverUrl(trainingBaseInfoSaveDTO.getCoverUrl());
        trainingDraft.setPurchaseEndTime(trainingBaseInfoSaveDTO.getPurchaseEndTime());
        trainingDraft.setDifficulty(trainingBaseInfoSaveDTO.getDifficulty());
        trainingDraft.setTrainPart(trainingBaseInfoSaveDTO.getTrainPart());
        trainingDraft.setCalorieBurn(trainingBaseInfoSaveDTO.getCalorieBurn());
        //2.3.未上架数据封装，已上架训练不能修改字段
        if (training == null) {
            //2.3.1.训练价格
            trainingDraft.setPrice(NumberUtils.null2Zero(trainingBaseInfoSaveDTO.getPrice()));
            //2.3.2.训练有效期
            trainingDraft.setValidDuration(trainingBaseInfoSaveDTO.getValidDuration());
            //2.3.3.训练状态
            trainingDraft.setStatus(TrainingStatus.NO_UP_SHELF.getStatus());
            //2.3.4.一级训练分类id
            trainingDraft.setFirstCateId(categoryIdList.get(0));
            //2.3.5.二级训练分类id
            trainingDraft.setSecondCateId(categoryIdList.get(1));
            //2.3.6.三级训练分类id
            trainingDraft.setThirdCateId(categoryIdList.get(2));
            //2.3.7.售卖模式
            trainingDraft.setFree(trainingBaseInfoSaveDTO.getFree() ? 1 : 0);
            //2.3.8.训练名称
            trainingDraft.setName(trainingBaseInfoSaveDTO.getName());
        }

        //3.操作
        if (trainingBaseInfoSaveDTO.getId() == null) {
            //3.1.新增训练草稿
            //3.1.1.新生成训练id
            Long id = IdWorker.getId();
            //3.1.2.设置训练id
            trainingContentDraft.setId(id);
            trainingDraft.setId(id);
            //3.1.3.设置训练编辑进度
            trainingDraft.setStep(TrainingConstants.TrainingStep.BASE_INFO);
            //3.1.4.插入训练草稿
            baseMapper.insert(trainingDraft);
            //3.1.5.插入训练草稿内容
            trainingContentDraftMapper.insert(trainingContentDraft);
        } else {
            //3.2.编辑训练草稿
            //3.2.1.设置训练id
            trainingContentDraft.setId(trainingBaseInfoSaveDTO.getId());
            trainingDraft.setId(trainingBaseInfoSaveDTO.getId());
            //3.2.2.更新训练草稿
            baseMapper.updateById(trainingDraft);
            //3.2.3.更新训练草稿内容
            trainingContentDraftMapper.updateById(trainingContentDraft);
        }
        //4.返回训练新增dto
        return TrainingSaveVO
                .builder()
                .id(trainingDraft.getId())
                .build();
    }

    @Override
    public TrainingBaseInfoVO getTrainingBaseInfo(Long id, Boolean see) {

        TrainingBaseInfoVO trainingBaseInfoVO = null;
        if (see) {
            //1.查询训练信息
            Training training = trainingMapper.selectById(id);
            if (training != null) {
                //1.1.查询训练对应的报名购买人数和退款人数
                TrainingPurchaseInfoDTO trainingPurchaseInfoDTO = tradeClient.getPurchaseInfoOfTraining(id);
                //1.2.组装数据
                trainingBaseInfoVO = BeanUtils.toBean(training, TrainingBaseInfoVO.class);
                //1.3.查询训练内容
                TrainingContent trainingContent = trainingContentMapper.selectById(id);
                //1.4.设置训练评分
                trainingBaseInfoVO.setCoureScore(NumberUtils.div(NumberUtils.null2Zero(training.getScore()) * 1.0, 10, 2));
                //1.5.设置报名人数
                trainingBaseInfoVO.setEnrollNum(trainingPurchaseInfoDTO.getEnrollNum());
                //1.6.设置跟练人数
                trainingBaseInfoVO.setStudyNum(workoutClient.countWorkoutSessionByTraining(id));
                //1.7.设置退款人数
                trainingBaseInfoVO.setRefundNum(trainingPurchaseInfoDTO.getRefundNum());
                //1.8.设置实付金额
                trainingBaseInfoVO.setRealPayAmount(trainingPurchaseInfoDTO.getRealPayAmount());
                //1.9.设置训练详情
                trainingBaseInfoVO.setDetail(trainingContent.getTrainingDetail());
                //1.10.设置训练介绍
                trainingBaseInfoVO.setIntroduce(trainingContent.getTrainingIntroduce());
                //1.11.设置训练适用人群
                trainingBaseInfoVO.setUsePeople(trainingContent.getUsePeople());
                //1.12.设置小节总数量
                trainingBaseInfoVO.setOutlineTotalNum(training.getSessionNum());
            }
        }
        //2.查询草稿信息
        if (trainingBaseInfoVO == null) {
            //2.1.查询草稿训练信息
            TrainingDraft trainingDraft = baseMapper.selectById(id);
            //2.2.有草稿训练信息
            if (trainingDraft != null) {
                //2.3.组装训练信息
                trainingBaseInfoVO = BeanUtils.toBean(trainingDraft, TrainingBaseInfoVO.class);
                //2.4.查询训练内容信息
                TrainingContentDraft trainingContentDraft = trainingContentDraftMapper.selectById(id);
                //2.5.设置训练详情
                trainingBaseInfoVO.setDetail(trainingContentDraft.getTrainingDetail());
                //2.6.设置训练介绍
                trainingBaseInfoVO.setIntroduce(trainingContentDraft.getTrainingIntroduce());
                //2.7.适用人群
                trainingBaseInfoVO.setUsePeople(trainingContentDraft.getUsePeople());
                //2.8.训练章节数
                trainingBaseInfoVO.setOutlineTotalNum(trainingDraft.getSessionNum());
                //2.9.设置训练评分
                trainingBaseInfoVO.setCoureScore(0d);
                //2.10.设置报名人数
                trainingBaseInfoVO.setEnrollNum(0);
                //2.11.设置跟练人数
                trainingBaseInfoVO.setStudyNum(0);
                //2.12.设置退款人数
                trainingBaseInfoVO.setRefundNum(0);
                //2.13.设置实付金额
                trainingBaseInfoVO.setRealPayAmount(0);
            }
        }
        if(trainingBaseInfoVO == null){
            return new TrainingBaseInfoVO();
        }

        //3.查询创建者，更新者姓名
        List<UserDTO> userDTOS = userClient.queryUserByIds(
                Arrays.asList(trainingBaseInfoVO.getCreater(), trainingBaseInfoVO.getUpdater())
                        .stream()
                        .distinct()
                        .collect(Collectors.toList())
        );
        if (CollUtils.isNotEmpty(userDTOS)) {
            //3.1.创建者，更新至id+name映射关系
            Map<Long, String> operatorMap = userDTOS
                    .stream()
                    .collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));
            //3.2.设置创建者姓名
            trainingBaseInfoVO.setCreaterName(operatorMap.get(trainingBaseInfoVO.getCreater()));
            //3.3.设置更新者姓名
            trainingBaseInfoVO.setUpdaterName(operatorMap.get(trainingBaseInfoVO.getUpdater()));
        }

        //4.训练分类信息
        List<Category> categories = categoryService.queryByIds(
                Arrays.asList(trainingBaseInfoVO.getFirstCateId(),
                        trainingBaseInfoVO.getSecondCateId(),
                        trainingBaseInfoVO.getThirdCateId()));
        if (CollUtils.isNotEmpty(categories)) {
            //4.1分类id和名称关系
            Map<Long, String> categoryIdAndName = categories
                    .stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName));
            //4.2.设置训练分类名称
            trainingBaseInfoVO.setCateNames(
                    StringUtils.format("{}/{}/{}",
                            categoryIdAndName.get(trainingBaseInfoVO.getFirstCateId()),
                            categoryIdAndName.get(trainingBaseInfoVO.getSecondCateId()),
                            categoryIdAndName.get(trainingBaseInfoVO.getThirdCateId()))
            );
        }
        return trainingBaseInfoVO;
    }

    @Override
    public void updateStep(Long id, Integer step) {
        //1.查询训练草稿
        TrainingDraft trainingDraft = baseMapper.selectById(id);
        TrainingDraft updateTrainingDraft = new TrainingDraft();
        updateTrainingDraft.setId(id);
        updateTrainingDraft.setCVersion(trainingDraft.getCVersion() + 1);
        //2.设置训练步骤，训练步骤只能前进不能后退
        if (trainingDraft.getStep() < step) {
            updateTrainingDraft.setStep(step);
        }else {
            updateTrainingDraft.setStep(trainingDraft.getStep());
        }
        //3.设置课时数，保存目录和保存题目两部进行修改
        if (TrainingConstants.TrainingStep.OUTLINE == step ||
                TrainingConstants.TrainingStep.ASSESSMENT == step) {
            //3.1题目保存和目录保存都会修改课时数量
            updateTrainingDraft.setSessionNum(trainingOutlineDraftService.totalSessionNums(id));
        }
        //4.更新課程狀態
        baseMapper.updateById(updateTrainingDraft);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void upShelf(Long id) {
        // 1.信息获取
        //1.1获取上架的训练草稿信息
        TrainingDraft trainingDraft = baseMapper.selectById(id);
        //1.2获取训练信息
        Training training = trainingMapper.selectById(id);
        boolean isFirstUpShelf = (training == null);

        // 2.校验训练
        checkBeforeUpShelf(id);

        //3.计算每个章节的课时视频时长
        Map<Long, Integer> mediaDurations = trainingOutlineDraftService.calculateMediaDuration(id);
        //3.1.计算训练视屏总时长
        int totalMediaDuration = mediaDurations
                .values()
                .stream()
                .mapToInt(p -> p)
                .sum();


        //4.草稿信息上架到正式环境
        //4.1训练教练信息
        trainingCoachDraftService.copyToShelf(id, isFirstUpShelf);
        //4.2 题目信息上架
        trainingOutlineDraftService.copyAssessmentToShelf(id, isFirstUpShelf);
        //4.3目录信息上架
        trainingOutlineDraftService.copyToShelf(id, isFirstUpShelf);
        //4.4 组装训练基本信息、训练内容信息
        TrainingContentDraft trainingContentDraft = trainingContentDraftMapper.selectById(id);
        TrainingContent trainingContent = BeanUtils.toBean(trainingContentDraft, TrainingContent.class);
        Training trainingToShelf = BeanUtils.toBean(trainingDraft, Training.class);
        //4.4.1.训练视频总时长
        trainingToShelf.setMediaDuration(totalMediaDuration);
        //4.4.2.训练有效期月数
        trainingToShelf.setValidDuration(trainingDraft.getValidDuration());
        //4.4.3.训练发布时间
        trainingToShelf.setPublishTime(DateUtils.now());
        //4.4.4.训练状态设置为已上架
        trainingToShelf.setStatus(TrainingStatus.SHELF.getStatus());
        //4.4.5.训练发布次数
        int publishTimes = (training == null) ?
                1 : NumberUtils.null2Zero(training.getPublishTimes()) + 1;
        trainingToShelf.setPublishTimes(publishTimes);
        // 4.4.6.评分
        trainingToShelf.setScore((int)(Math.random() * 10) + 40);

        //4.5.首次上架
        if (isFirstUpShelf) {
            //4.5.1.插入训练内容信息
            int result = trainingContentMapper.insert(trainingContent);
            if (result <= 0) {
                throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
            }
            //4.5.2.插入训练基本信息
            result = trainingMapper.insert(trainingToShelf);
            if (result <= 0) {
                throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
            }
            //4.5.1.删除训练草稿基本信息
            baseMapper.deleteById(id);
            //4.5.2.删除训练草稿内容信息
            trainingContentDraftMapper.deleteById(id);
        } else {
            //4.6.再次上架
            //4.6.1.更新正式训练内容信息
            int result = trainingContentMapper.updateById(trainingContent);
            if (result <= 0) {
                throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
            }
            //4.6.2.更新正式训练基本信息
            result = trainingMapper.updateVariableById(trainingToShelf);
            if (result <= 0) {
                throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
            }
            //4.6.3.删除训练草稿基本信息
            baseMapper.deleteById(id);
            //4.6.4.删除训练草稿内容信息
            trainingContentDraftMapper.deleteById(id);

        }
        //5.训练上架mq
        rabbitMqHelper.sendAsyn(MqConstants.Exchange.TRAINING_EXCHANGE,
                MqConstants.Key.TRAINING_UP_KEY,
                id,
                200L);
    }

    @Override
    public void checkBeforeUpShelf(Long id) {
        //1.获取上架的训练草稿信息
        TrainingDraft trainingDraft = baseMapper.selectById(id);
        //1.1.获取训练信息
        Training training = trainingMapper.selectById(id);
        //2.训练校验
        //2.1.训练上架幂等校验
        if (trainingDraft == null && training != null) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_UP_SHELF_AREADY);
        }
        //2.2.训练id不存在的训练无法上架
        if (trainingDraft == null && training == null) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_UP_SHELF_NOT_FOUND_TRAINING);

        }
        //2.3.草稿信息不完整无法上架
        if (trainingDraft.getStep() != TrainingConstants.TrainingStep.COACH) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_UP_SHELF_INFO_INCOMPLETE);
        }
        //训练
        //2.4.已上架/已完结训练不能上架
        if (training != null && training.getStatus() != TrainingStatus.DOWN_SHELF.getStatus()) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_UP_SHELF_STATE_WRONG);
        }
        //2.5.校验训练结束时间
        if(trainingDraft.getPurchaseEndTime().isBefore(DateUtils.now())){
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_UP_SHELF_PURCHASE_ILLEGAL);
        }
        //2.6.首次上架校验逻辑
        if (training == null) {
            //2.5.1.统计同名的训练数量
            int sameNameNum = trainingMapper.countSameName(trainingDraft.getName());
            //2.5.2.有同名训练不能上架
            if (sameNameNum > 0) {
                throw new BadRequestException(TrainingErrorInfo.Msg.TRAINING_SAVE_NAME_EXISTS);
            }
        }
        //2.7.校验训练目录
        trainingOutlineDraftService.checkOutlineInfoImplated(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void downShelf(Long id) {
        //1.查询训练基本信息
        Training training = trainingService.getById(id);
        //1.1训练状态判断
        if (training == null || !training.getStatus().equals(TrainingStatus.SHELF.getStatus())) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_DOWN_SHELF_FAILD);
        }
        //2.先更新训练状态
        trainingService.updateStatus(id, TrainingStatus.DOWN_SHELF.getStatus());
        //3.训练基本信息和内容信息copy到草稿中
        baseMapper.insertFromTraining(id);
        //4.训练内容copy到草稿中
        trainingContentDraftMapper.insertFromTrainingContent(id);
        //5.目录内容copy到草稿中
        trainingOutlineDraftMapper.insertFromTrainingOutline(id);
        //6.训练题目copy到草稿中
        copyAssessment2Draft(id);
        //7.训练教练copy到草稿中
        trainingCoachDraftMapper.insertFromTrainingCoach(id);
        //8.下架mq广播
        rabbitMqHelper.send(MqConstants.Exchange.TRAINING_EXCHANGE, MqConstants.Key.TRAINING_DOWN_KEY, id);
    }

    @GlobalTransactional
    public void copyAssessment2Draft(Long trainingId) {
        // 1.查询训练有关的小节信息
        List<Long> sessionIds = trainingOutlineDraftMapper.getSessionIdByTrainingId(trainingId);
        if (CollUtils.isEmpty(sessionIds)) {
            log.error("训练小节数据为空");
            return;
        }
        // 2.查询题目关系
        List<QuestionBizDTO> qbs = examClient.queryQuestionIdsByBizIds(sessionIds);
        if (CollUtils.isEmpty(qbs)) {
            return;
        }
        List<TrainingOutlineAssessmentDraft> list = qbs.stream().map(q -> new TrainingOutlineAssessmentDraft()
                .setTrainingId(trainingId).setOutlineId(q.getBizId()).setAssessmentId(q.getQuestionId())
        ).collect(Collectors.toList());
        // 3.保存到草稿表
        trainingOutlineAssessmentDraftMapper.batchInsert(list);
    }

    @Override
    public TrainingDTO getTrainingDTOById(Long id) {
        //1.查询训练草稿基础信息
        TrainingDraft trainingDraft = baseMapper.selectById(id);
        //1.1.判空
        if (trainingDraft == null) {
            return new TrainingDTO();
        }
        //2.查询训练教练列表，并去序号的第一个
        LambdaQueryWrapper<TrainingCoachDraft> queryWrapper =
                Wrappers.lambdaQuery(TrainingCoachDraft.class)
                .eq(TrainingCoachDraft::getTrainingId, id)
                .orderBy(true, false, TrainingCoachDraft::getCIndex)
                .last(true, "limit 1");
        //2.1.查询训练教练信息
        TrainingCoachDraft trainingCoachDraft = trainingCoachDraftMapper.selectOne(queryWrapper);
        //3.组装数据
        TrainingDTO trainingDTO = BeanUtils.toBean(trainingDraft, TrainingDTO.class);
        //3.1.设置训练分类，一级、二级、三级训练分类
        trainingDTO.setCategoryIdLv1(trainingDraft.getFirstCateId());
        trainingDTO.setCategoryIdLv2(trainingDraft.getSecondCateId());
        trainingDTO.setCategoryIdLv3(trainingDraft.getThirdCateId());
        //3.2.设置训练视频播放总时长
        trainingDTO.setDuration(trainingDraft.getMediaDuration());
        //3.3.设置训练总小节数
        trainingDTO.setSessions(trainingDraft.getSessionNum());
        //3.4.设置训练教练id
        if (trainingCoachDraft != null) {
            trainingDTO.setCoach(trainingCoachDraft.getCoachId());
        } else {
            trainingDTO.setCoach(0L);
        }

        return trainingDTO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void delete(Long id) {
        //1.删除训练草稿
        baseMapper.deleteById(id);
        //2.删除训练内容草稿
        trainingContentDraftMapper.deleteById(id);
        //3.删除训练题目关系草稿
        trainingOutlineAssessmentDraftMapper.deleteByTrainingId(id);
        //4.删除训练目录草稿
        trainingOutlineDraftMapper.deleteByTrainingId(id, Arrays.asList(
                TrainingConstants.OutlineType.PHASE,
                TrainingConstants.OutlineType.SESSION,
                TrainingConstants.OutlineType.PRATICE
        ));
        //5.删除训练教练关系草稿
        trainingCoachDraftMapper.deleteByTrainingId(id);
    }

    @Override
    public PageDTO<TrainingPageVO> queryForPage(TrainingPageQuery trainingPageQuery) {
        //1.训练草稿分页查询条件
        LambdaQueryWrapper<TrainingDraft> queryWrapper =
                SqlWrapperUtils.toLambdaQueryWrapper(trainingPageQuery, TrainingDraft.class);
        //1.1训练查询条件-更新时间
        queryWrapper.between(
                ObjectUtils.isNotEmpty(trainingPageQuery.getBeginTime()) &&
                        ObjectUtils.isNotEmpty(trainingPageQuery.getEndTime()),
                TrainingDraft::getUpdateTime,
                trainingPageQuery.getBeginTime(),
                trainingPageQuery.getEndTime());
        //1.2训练查询条件-搜索关键字
        queryWrapper.like(StringUtils.isNotEmpty(trainingPageQuery.getKeyword()),
                TrainingDraft::getName, trainingPageQuery.getKeyword());
        queryWrapper.eq(ObjectUtils.isNotEmpty(trainingPageQuery.getDifficulty()),
                TrainingDraft::getDifficulty, trainingPageQuery.getDifficulty());
        queryWrapper.eq(StringUtils.isNotEmpty(trainingPageQuery.getTrainPart()),
                TrainingDraft::getTrainPart, trainingPageQuery.getTrainPart());
        //1.3.分页查询查询数据
        Page<TrainingDraft> page = page(trainingPageQuery.toMpPage(), queryWrapper);
        //1.4.分页查询结果判空
        if (CollUtils.isEmpty(page.getRecords())) {
            return PageDTO.empty(page);
        }
        //2.组装数据查询
        //2.1.训练更新人id列表
        List<Long> updaterList = page.getRecords().stream()
                .map(TrainingDraft::getUpdater)
                .collect(Collectors.toList());
        //2.2.查询更新人用户信息
        List<UserDTO> userDTOS = userClient.queryUserByIds(updaterList);
        //2.3.转化更新人用户id+name 映射关系
        Map<Long, String> updaterMap =
                CollUtils.isEmpty(updaterList) ?
                        new HashMap<>() : userDTOS.stream().collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));
        //2.4.查询训练分类列表
        List<Category> list = categoryService.list();
        //2.5.转化训练分类id+name映射关系
        Map<Long, String> categoryNameMap =
                CollUtils.isEmpty(list) ?
                        new HashMap<>() : list.stream().collect(Collectors.toMap(Category::getId, Category::getName));
        //2.6.训练id列表
        List<Long> trainingIdList = page.getRecords().stream().map(TrainingDraft::getId).collect(Collectors.toList());
        //2.7.统计训练报名人数map
        Map<Long, Integer> peoNumOfTrainingMap = tradeClient.countEnrollNumOfTraining(trainingIdList);
        //3.数据封装
        return PageDTO.of(page, TrainingPageVO.class, (training, trainingPageVO) -> {
            //3.1.拼接训练分类
            String categories = StringUtils.format("{}/{}/{}",
                    categoryNameMap.get(training.getFirstCateId()),
                    categoryNameMap.get(training.getSecondCateId()),
                    categoryNameMap.get(training.getThirdCateId()));
            //3.2.设置训练分类
            trainingPageVO.setCategories(categories);
            //3.3.设置训练更新人
            trainingPageVO.setUpdaterName(updaterMap.get(training.getUpdater()));
            //3.4.设置训练报名人数
            trainingPageVO.setSold(NumberUtils.null2Zero(peoNumOfTrainingMap.get(training.getId())));
            //3.5.设置训练总课时数
            trainingPageVO.setSessions(training.getSessionNum());
        });
    }

    @Override
    public NameExistVO checkName(String name, Long id) {
        //1.训练草稿同名查询条件
        LambdaQueryWrapper<TrainingDraft> queryWrapper =
                Wrappers.lambdaQuery(TrainingDraft.class)
                        .eq(TrainingDraft::getName, name)
                        .last(id != null, " and id !=" + id);
        //2.统计同名训练数量
        Integer num = baseMapper.selectCount(queryWrapper);
        //3.返回同名训练VO
        return new NameExistVO(num > 0);
    }

    @Override
    public List<Long> queryExists(List<Long> idList) {
        //1.查询草稿训练基础信息列表
        List<TrainingDraft> trainings = baseMapper.selectBatchIds(idList);
        //1.1.草稿训练信息列表判空
        if (CollUtils.isEmpty(trainings)) {
            return null;
        }
        //2.组装数据
        return trainings.stream()
                .map(TrainingDraft::getId)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Integer> countTrainingNumOfCategory() {
        //1.查询所有训练草稿
        List<TrainingDraft> trainings = baseMapper.selectList(null);
        Map<Long, Integer> cateIdAndNumMap = new HashMap<>();
        //2.遍历统计每个训练分类拥有的训练数量
        for (TrainingDraft training : trainings) {
            //2.1.统计一级训练分类训练数量
            Integer firstCateNum = cateIdAndNumMap.get(training.getFirstCateId());
            cateIdAndNumMap.put(training.getFirstCateId(), firstCateNum == null ? 1 : firstCateNum + 1);
            //2.2.统计二级训练分类训练数量
            Integer secondCateNum = cateIdAndNumMap.get(training.getSecondCateId());
            cateIdAndNumMap.put(training.getSecondCateId(), secondCateNum == null ? 1 : secondCateNum + 1);
            //2.3.统计三级训练分类训练数量
            Integer thirdCateNum = cateIdAndNumMap.get(training.getThirdCateId());
            cateIdAndNumMap.put(training.getThirdCateId(), thirdCateNum == null ? 1 : thirdCateNum + 1);
        }
        return cateIdAndNumMap;
    }

    private void checkSameTraining(Long id, String name) {
        //1.查询正式环境是否有同名训练
        int countSameNameNum = trainingMapper.countSameName(name);
        //1.1.同名训练数据判0
        if (countSameNameNum > 0) { //名称已经存在，提交时做双重校验
            throw new BadRequestException(TrainingErrorInfo.Msg.TRAINING_SAVE_NAME_EXISTS);
        }
        //2.查询正式环境是否有同名训练
        countSameNameNum = baseMapper.countByNameAndId(name, id);
        //2.1.同名训练数据判0
        if (countSameNameNum > 0) {
            throw new BadRequestException(TrainingErrorInfo.Msg.TRAINING_SAVE_NAME_EXISTS);
        }
    }
}

