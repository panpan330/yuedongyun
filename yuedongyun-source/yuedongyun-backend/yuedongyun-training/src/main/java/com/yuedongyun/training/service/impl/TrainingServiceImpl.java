package com.yuedongyun.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.api.client.exam.ExamClient;
import com.yuedongyun.api.client.workout.WorkoutClient;
import com.yuedongyun.api.client.trade.TradeClient;
import com.yuedongyun.api.client.user.UserClient;
import com.yuedongyun.api.dto.IdAndNumDTO;
import com.yuedongyun.api.dto.training.*;
import com.yuedongyun.api.dto.workout.WorkoutSessionDTO;
import com.yuedongyun.api.dto.workout.WorkoutRecordDTO;
import com.yuedongyun.api.dto.user.UserDTO;
import com.yuedongyun.common.autoconfigure.mq.RabbitMqHelper;
import com.yuedongyun.common.constants.ErrorInfo;
import com.yuedongyun.common.constants.MqConstants;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.common.exceptions.BadRequestException;
import com.yuedongyun.common.exceptions.DbException;
import com.yuedongyun.common.utils.*;
import com.yuedongyun.training.constants.TrainingErrorInfo;
import com.yuedongyun.training.constants.TrainingStatus;
import com.yuedongyun.training.constants.RedisContants;
import com.yuedongyun.training.domain.dto.TrainingPageQuery;
import com.yuedongyun.training.domain.dto.TrainingSimpleInfoListDTO;
import com.yuedongyun.training.domain.po.Category;
import com.yuedongyun.training.domain.po.Category3PO;
import com.yuedongyun.training.domain.po.Training;
import com.yuedongyun.training.domain.po.TrainingCoach;
import com.yuedongyun.training.domain.vo.*;
import com.yuedongyun.training.mapper.TrainingDraftMapper;
import com.yuedongyun.training.mapper.TrainingMapper;
import com.yuedongyun.training.mapper.TrainingCoachMapper;
import com.yuedongyun.training.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 草稿训练 服务实现类
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
@Service
@Slf4j
public class TrainingServiceImpl extends ServiceImpl<TrainingMapper, Training> implements ITrainingService {

    @Autowired
    private TrainingCoachMapper trainingCoachMapper;

    @Autowired
    private TrainingDraftMapper trainingDraftMapper;

    @Autowired
    private ITrainingDraftService trainingDraftService;

    @Autowired
    private RabbitMqHelper rabbitMqHelper;

    @Autowired
    private ITrainingOutlineService trainingOutlineService;

    @Autowired
    private ITrainingCoachService trainingCoachService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private TradeClient tradeClient;

    @Autowired
    private ExamClient examClient;

    @Autowired
    private WorkoutClient workoutClient;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void updateStatus(Long id, Integer status) {
        //1.组装数据
        Training training = new Training();
        training.setId(id);
        training.setStatus(status);
        training.setUpdateTime(LocalDateTime.now());
        //2.更新数据
        int result = baseMapper.updateById(training);
        //3.判断直接条数是1条
        if (result != 1) {
            throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
        }
    }

    @Override
    public TrainingDTO getTrainingDTOById(Long id) {
        // 1.查询训练信息
        Training training = baseMapper.selectById(id);
        //1.1判空
        if (training == null) {
            return null;
        }
        //2.教练查询条件
        LambdaQueryWrapper<TrainingCoach> queryWrapper =
                Wrappers.lambdaQuery(TrainingCoach.class)
                        .eq(TrainingCoach::getTrainingId, id)
                        .orderBy(true, false, TrainingCoach::getCIndex)
                        .last(true, "limit 1");
        // 2.查询教练
        List<TrainingCoach> trainingCoachs = trainingCoachMapper.selectList(queryWrapper);

        // 3.训练数据封装
        TrainingDTO trainingDTO = BeanUtils.toBean(training, TrainingDTO.class);
        //3.1.一级训练分类
        trainingDTO.setCategoryIdLv1(training.getFirstCateId());
        //3.2.二级训练分类
        trainingDTO.setCategoryIdLv2(training.getSecondCateId());
        //3.3.三级训练分类
        trainingDTO.setCategoryIdLv3(training.getThirdCateId());
        //3.4.媒资信息
        trainingDTO.setDuration(training.getMediaDuration());
        //3.5.训练发布时间
        trainingDTO.setPublishTime(training.getCreateTime());
        //3.6.训练小节数量
        trainingDTO.setSessions(training.getSessionNum());
        //3.7.训练第一位教练
        if (CollUtils.isNotEmpty(trainingCoachs)) {
            trainingDTO.setCoach(trainingCoachs.get(0).getCoachId());
        } else {
            trainingDTO.setCoach(0L);
        }

        // 4.统计训练销量
        Map<Long, Integer> peoNumOfTrainingMap = tradeClient.countEnrollNumOfTraining(CollUtils.singletonList(id));
        if (CollUtils.isNotEmpty(peoNumOfTrainingMap)) {
            trainingDTO.setSold(peoNumOfTrainingMap.getOrDefault(id, 0));
        }
        //5.返回数据
        return trainingDTO;

    }

    @Override
    public void delete(Long id) {
        //1.删除草稿信息
        trainingDraftService.delete(id);
        //2.发送删除草稿mq
        rabbitMqHelper.send(MqConstants.Exchange.TRAINING_EXCHANGE, MqConstants.Key.TRAINING_DELETE_KEY, id);
    }

    @Override
    public List<TrainingSimpleInfoDTO> getSimpleInfoList(TrainingSimpleInfoListDTO trainingSimpleInfoListDTO) {
        //1.训练查询条件
        LambdaQueryWrapper<Training> queryWrapper =
                Wrappers.lambdaQuery(Training.class)
                        .in(CollUtils.isNotEmpty(trainingSimpleInfoListDTO.getThirdOutlineIds()),
                                Training::getThirdCateId, trainingSimpleInfoListDTO.getThirdOutlineIds())
                        .in(CollUtils.isNotEmpty(trainingSimpleInfoListDTO.getIds()),
                                Training::getId, trainingSimpleInfoListDTO.getIds());
        //2.查询训练
        List<Training> trainings = baseMapper.selectList(queryWrapper);
        //3.训练信息转化
        return BeanUtils.copyList(trainings, TrainingSimpleInfoDTO.class);
    }

    @Override
    public List<Training> queryByCategoryIdAndLevel(Long categoryId, Integer level) {
        //1.训练基本信息查询条件
        LambdaQueryWrapper<Training> queryWrapper =
                Wrappers.lambdaQuery(Training.class)
                        .eq(level == 1, Training::getFirstCateId, categoryId) //一级训练分类
                        .eq(level == 2, Training::getSecondCateId, categoryId) //二级训练分类
                        .eq(level == 3, Training::getThirdCateId, categoryId);//三级训练分类
        //2.查询训练基本信息
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public NameExistVO checkName(String name, Long id) {
        //1.正式训练同名训练数量
        LambdaQueryWrapper<Training> queryWrapper =
                Wrappers.lambdaQuery(Training.class)
                        .eq(Training::getName, name)
                        .last(id != null, " and id !=" + id);
        //2.统计数量
        Integer num = baseMapper.selectCount(queryWrapper);
        if (num > 0) {
            return NameExistVO.EXISTED;
        }
        //3.统计草稿训练同名数量
        return trainingDraftService.checkName(name, id);
    }

    @Override
    public List<Long> queryExists(List<Long> idList, List<Integer> statusList) {
        //1.指定状态的训练id存在查询条件
        LambdaQueryWrapper<Training> queryWrapper =
                Wrappers.lambdaQuery(Training.class)
                        .in(Training::getId, idList)
                        .in(Training::getStatus, statusList);
        //2.根据条件查询训练
        List<Training> trainings = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(trainings)) {
            return null;
        }
        //3.组装数据
        return trainings.stream()
                .map(Training::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> queryTrainingIdByName(String name) {
        // 1.查询条件
        LambdaQueryWrapper<Training> queryWrapper =
                Wrappers.lambdaQuery(Training.class)
                .like(Training::getName, name);
        // 2.查询数据
        List<Training> trainings = baseMapper.selectList(queryWrapper);
        // 3.转换出训练id列表
        return trainings.stream()
                .map(Training::getId)
                .collect(Collectors.toList());
    }


    @Override
    public TrainingAndSessionVO queryTrainingAndOutlinelogById(Long trainingId) {
        // 1.获取当前用户
        Long userId = UserContext.getUser();
        // 2.查询训练详情
        TrainingFullInfoDTO training = getInfoById(trainingId, true, true);
        if (training == null) {
            return null;
        }
        // 3.组织VO
        TrainingAndSessionVO vo = new TrainingAndSessionVO();
        vo.setId(trainingId);
        vo.setName(training.getName());
        vo.setSessions(training.getSessionNum());
        vo.setCoverUrl(training.getCoverUrl());
        // 4.查询教练信息
        List<UserDTO> coaches = userClient.queryUserByIds(training.getCoachIds());
        if (CollUtils.isNotEmpty(coaches)) {
            UserDTO coach = coaches.get(0);
            vo.setCoachName(coach.getName());
            vo.setCoachIcon(coach.getIcon());
        }
        // 5.组装小节信息
        List<OutlineDTO> outlines = training.getPhases();
        List<PhaseVO> phases = new ArrayList<>(outlines.size());
        for (OutlineDTO c : outlines) {
            PhaseVO cv = new PhaseVO();
            cv.setId(c.getId());
            cv.setName(c.getName());
            cv.setIndex(c.getIndex());
            cv.setMediaDuration(c.getMediaDuration());
            List<SessionVO> sessions = BeanUtils.copyToList(c.getSessions(), SessionVO.class);
            cv.setSessions(sessions);
            phases.add(cv);
        }
        vo.setPhases(phases);
        // 6.查询跟练进度
        if (workoutClient == null) {
            return vo;
        }
        // 6.1.查询跟练记录
        WorkoutSessionDTO sessionDTO = workoutClient.queryWorkoutRecordByTraining(trainingId);
        if (sessionDTO == null) {
            // 没有查询到课表信息，说明是免费试看，直接返回
            return vo;
        }
        vo.setSessionId(sessionDTO.getId());
        if (CollUtils.isEmpty(sessionDTO.getRecords())) {
            // 有课表信息，但是没有跟练记录，不用处理进度问题了，直接返回
            return vo;
        }
        List<WorkoutRecordDTO> records = sessionDTO.getRecords();
        // 6.2.获取最近跟练的记录。由于查询时按照跟练时间排序，第一条记录就是最近跟练的小节记录
        Long latestSessionId = sessionDTO.getLatestSessionId();
        if(latestSessionId == null) {
            latestSessionId = records.get(0).getSessionId();
        }
        vo.setLatestSessionId(latestSessionId);
        // 6.3.处理记录为一个map
        Map<Long, WorkoutRecordDTO> rMap = records.stream()
                .collect(Collectors.toMap(WorkoutRecordDTO::getSessionId, r -> r));
        // 6.4.填充跟练进度到章节中
        for (PhaseVO phase : vo.getPhases()) {
            for (SessionVO session : phase.getSessions()) {
                WorkoutRecordDTO r = rMap.get(session.getId());
                if (r == null) continue;
                session.setFinished(r.getFinished());
                session.setMoment(r.getMoment());
            }
        }
        return vo;
    }

    @Override
    public List<SubNumAndTrainingNumDTO> countAssessmentNumAndTrainingNumOfCoach(List<Long> coachIds) {
        // 1.统计数据
        // 1.1.教练id和训练数量(已上架、已下架、已过期)
        Map<Long, Integer> coachIdAndTrainingNumMap =
                IdAndNumDTO.toMap(baseMapper.countTrainingNumOfCoach(coachIds));
        // 1.2.待上架
        Map<Long, Integer> coachIdAndTrainingNumMap2 =
                IdAndNumDTO.toMap(trainingDraftMapper.countTrainingNumOfCoach(coachIds));
        // 1.3.统计教练的出题数量
        Map<Long, Integer> coachIdAndAssessmentNumMap = examClient.countAssessmentNumOfCoach(coachIds);

        // 2.遍历教练id
        List<SubNumAndTrainingNumDTO> subNumAndTrainingNumDTOS = new ArrayList<>();
        for (Long coachId : coachIds) {
            subNumAndTrainingNumDTOS.add(new SubNumAndTrainingNumDTO(
                    //2.1.设置教练id
                    coachId,
                    //2.2.设置教练训练数量
                    NumberUtils.null2Zero(coachIdAndTrainingNumMap.get(coachId)) +
                            NumberUtils.null2Zero(coachIdAndTrainingNumMap2.get(coachId)),
                    //2.3.设置教练出题数量
                    NumberUtils.null2Zero(coachIdAndAssessmentNumMap.get(coachId))));
        }
        return subNumAndTrainingNumDTOS;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int trainingFinished() {
        //1.完结训练查询条件
        LambdaQueryWrapper<Training> queryWrapper =
                Wrappers.lambdaQuery(Training.class)
                        .le(Training::getPurchaseEndTime, LocalDateTime.now())
                        .in(Training::getStatus,
                                List.of(TrainingStatus.DOWN_SHELF.getStatus(),
                                        TrainingStatus.SHELF.getStatus()));

        //1.2.查询完结训练
        List<Training> trainings = baseMapper.selectList(queryWrapper);
        //1.3.完结训练判空
        if (CollUtils.isEmpty(trainings)) {
            return 0;
        }
        //2.组装数据
        List<Training> updateTrainings = new ArrayList<>();
        for (Training training : trainings) {
            Training updateTraining = new Training();
            //2.1.设置训练id
            updateTraining.setId(training.getId());
            //2.2.设置训练状态-已完结
            updateTraining.setStatus(TrainingStatus.FINISHED.getStatus());
            updateTrainings.add(updateTraining);
        }
        //3.批量完结训练
        updateBatchById(updateTrainings);
        //4.发送训练完结mq
        sendFinishedTraining(trainings);
        //5.清理草稿
        for (Training training: trainings){
            trainingDraftService.delete(training.getId());
        }

        return updateTrainings.size();
    }

    @Override
    @Cacheable(cacheNames = RedisContants.Formatter.STATISTICS_TRAINING_NUM_CATE)
    public Map<Long, Integer> countTrainingNumOfCategory() {
        //1.统计训练分类拥有已上架、已完成的训练数量
        Map<Long, Integer> nomalTrainingNumOfCategory =
                countNomalTrainingNumOfCategory();
        //2.统计训练分类待上架，已下架的训练分类的数量
        Map<Long, Integer> draftTrainingNumOfCategory =
                trainingDraftService.countTrainingNumOfCategory();
        //3.两组数据聚合
        return CollUtils.union(nomalTrainingNumOfCategory, draftTrainingNumOfCategory);
    }

    @Override
    @Cacheable(cacheNames = RedisContants.Formatter.CATEGORY_ID_LIST_HAVE_TRAINING)
    public List<Long> getCategoryIdListWithTraining() {
        // 1.查询条件
        List<Category3PO> category3s = baseMapper.queryCategoryIdWithTraining();
        // 1.1.判空
        if(CollUtils.isEmpty(category3s)) {
            return new ArrayList<>();
        }
        // 2.将训练分类id设置到categoryIdList
        List<Long> categoryIdList = new ArrayList<>();
        category3s.stream().forEach(category3->{
            category3.setId(categoryIdList);
        });
        // 2.1.去重，并返回数据
        return categoryIdList.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Integer countTrainingNumOfCategory(Long categoryId) {
        //1.训练统计条件
        LambdaQueryWrapper<Training> queryWrapper =
                Wrappers.lambdaQuery(Training.class)
                        .or().eq(Training::getFirstCateId, categoryId)
                        .or().eq(Training::getSecondCateId, categoryId)
                        .or().eq(Training::getThirdCateId, categoryId);
        //2.统计训练数量
        return baseMapper.selectCount(queryWrapper);
    }

    @Override
    public TrainingFullInfoDTO getInfoById(Long id, boolean withOutline, boolean withCoaches) {
        // 1.查询训练基本信息
        Training training = baseMapper.selectById(id);
        if (training == null) {
            throw new BadRequestException(TrainingErrorInfo.Msg.TRAINING_CHECK_NOT_EXISTS);
        }
        // 2.转换vo
        TrainingFullInfoDTO trainingFullInfoDTO = BeanUtils.toBean(training, TrainingFullInfoDTO.class);

        // 3.查询目录信息
        if (withOutline) {
            trainingFullInfoDTO.setPhases(trainingOutlineService.queryTrainingOutlines(id, true));
        }
        // 4.查询教练信息
        if (withCoaches) {
            trainingFullInfoDTO.setCoachIds(trainingCoachService.getCoachIdOfTraining(id));
        }
        return trainingFullInfoDTO;
    }

    @Override
    public PageDTO<TrainingPageVO> queryForPage(TrainingPageQuery trainingPageQuery) {
        //1.训练查询条件
        LambdaQueryWrapper<Training> queryWrapper =
                SqlWrapperUtils.toLambdaQueryWrapper(trainingPageQuery, Training.class);
        //1.1.训练条件-更新时间
        queryWrapper.between(
                ObjectUtils.isNotEmpty(trainingPageQuery.getBeginTime())
                        && ObjectUtils.isNotEmpty(trainingPageQuery.getEndTime()),
                Training::getUpdateTime,
                trainingPageQuery.getBeginTime(),
                trainingPageQuery.getEndTime());
        //1.2.训练查询条件-名称
        queryWrapper.like(
                StringUtils.isNotEmpty(trainingPageQuery.getKeyword()),
                Training::getName,
                trainingPageQuery.getKeyword());
        queryWrapper.eq(ObjectUtils.isNotEmpty(trainingPageQuery.getDifficulty()),
                Training::getDifficulty, trainingPageQuery.getDifficulty());
        queryWrapper.eq(StringUtils.isNotEmpty(trainingPageQuery.getTrainPart()),
                Training::getTrainPart, trainingPageQuery.getTrainPart());
        //1.3.分页查询数据
        Page<Training> page = page(trainingPageQuery.toMpPage(), queryWrapper);
        //1.4.分页数据判空
        if (CollUtils.isEmpty(page.getRecords())) {
            return PageDTO.empty(page);
        }
        //2.训练更新人id列表
        List<Long> updaterList = page
                .getRecords()
                .stream()
                .map(Training::getUpdater)
                .collect(Collectors.toList());
        //2.1查询更新人用户信息
        List<UserDTO> userDTOS = userClient.queryUserByIds(updaterList);
        //2.2.转化训练更新人id+name map
        Map<Long, String> updaterMap =
                CollUtils.isEmpty(updaterList) ?
                        new HashMap<>()
                        : userDTOS
                        .stream()
                        .collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));
        //3.获取所有训练分类信息
        List<Category> list = categoryService.list();
        //3.1.转化训练分类id+name map关系
        Map<Long, String> categoryNameMap =
                CollUtils.isEmpty(list) ?
                        new HashMap<>()
                        : list.stream()
                        .collect(Collectors.toMap(Category::getId, Category::getName));
        //4.训练id列表
        List<Long> trainingIdList = page
                .getRecords()
                .stream()
                .map(Training::getId)
                .collect(Collectors.toList());
        //4.1.统计每个训练报名人数map关系
        Map<Long, Integer> peoNumOfTrainingMap = tradeClient.countEnrollNumOfTraining(trainingIdList);
        //5.组装数据
        return PageDTO.of(page, TrainingPageVO.class, (training, trainingPageVO) -> {
            //5.1.拼接训练分类名称
            String categories = StringUtils.format("{}/{}/{}",
                    categoryNameMap.get(training.getFirstCateId()),
                    categoryNameMap.get(training.getSecondCateId()),
                    categoryNameMap.get(training.getThirdCateId()));
            //5.2.设置训练分类名称
            trainingPageVO.setCategories(categories);
            //5.3.设置训练更新人姓名
            trainingPageVO.setUpdaterName(updaterMap.get(training.getUpdater()));
            //5.4.设置训练报名人数
            trainingPageVO.setSold(NumberUtils.null2Zero(peoNumOfTrainingMap.get(training.getId())));
            //5.5.设置训练小节数
            trainingPageVO.setSessions(training.getSessionNum());
        });
    }

    /**
     * 异步发送训练完结mq
     *
     * @param finishedTraining
     */
    private void sendFinishedTraining(List<Training> finishedTraining) {
        //1.遍历发送训练完结mq
        for (Training training : finishedTraining) {
            rabbitMqHelper.sendAsyn(MqConstants.Exchange.TRAINING_EXCHANGE,
                    MqConstants.Key.TRAINING_EXPIRE_KEY,
                    training.getId());
        }
    }

    /**
     * 统计训练分类上架和已完结训练的数量
     *
     * @return
     */
    private Map<Long, Integer> countNomalTrainingNumOfCategory() {
        //1.查询条件
        LambdaQueryWrapper<Training> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Training::getStatus,
                Arrays.asList(TrainingStatus.SHELF.getStatus(), TrainingStatus.FINISHED.getStatus()));
        //2.查询数据
        List<Training> trainings = baseMapper.selectList(queryWrapper);
        Map<Long, Integer> cateIdAndNumMap = new HashMap<>();
        //3.统计每个分类的训练数量
        for (Training training : trainings) {
            //3.1一级分类数量
            Integer firstCateNum = cateIdAndNumMap.get(training.getFirstCateId());
            cateIdAndNumMap.put(training.getFirstCateId(), firstCateNum == null ? 1 : firstCateNum + 1);
            //3.2二级分类数量
            Integer secondCateNum = cateIdAndNumMap.get(training.getSecondCateId());
            cateIdAndNumMap.put(training.getSecondCateId(), secondCateNum == null ? 1 : secondCateNum + 1);
            //3.3三级分类数量够
            Integer thirdCateNum = cateIdAndNumMap.get(training.getThirdCateId());
            cateIdAndNumMap.put(training.getThirdCateId(), thirdCateNum == null ? 1 : thirdCateNum + 1);
        }
        return cateIdAndNumMap;
    }
}

