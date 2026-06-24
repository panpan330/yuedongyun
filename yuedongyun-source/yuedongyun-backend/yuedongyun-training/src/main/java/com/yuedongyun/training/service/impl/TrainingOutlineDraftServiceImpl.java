package com.yuedongyun.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.api.client.exam.ExamClient;
import com.yuedongyun.api.dto.exam.QuestionBizDTO;
import com.yuedongyun.api.dto.exam.QuestionDTO;
import com.yuedongyun.common.constants.ErrorInfo;
import com.yuedongyun.common.exceptions.BizIllegalException;
import com.yuedongyun.common.exceptions.DbException;
import com.yuedongyun.common.utils.*;
import com.yuedongyun.common.validate.Checker;
import com.yuedongyun.training.constants.TrainingConstants;
import com.yuedongyun.training.constants.TrainingErrorInfo;
import com.yuedongyun.training.constants.TrainingStatus;
import com.yuedongyun.training.domain.dto.OutlineSaveDTO;
import com.yuedongyun.training.domain.dto.OutlineAssessmentDTO;
import com.yuedongyun.training.domain.dto.TrainingMediaDTO;
import com.yuedongyun.training.domain.po.*;
import com.yuedongyun.training.domain.vo.OutlineSimpleAssessmentVO;
import com.yuedongyun.training.domain.vo.OutlineVO;
import com.yuedongyun.training.mapper.TrainingOutlineAssessmentDraftMapper;
import com.yuedongyun.training.mapper.TrainingOutlineAssessmentMapper;
import com.yuedongyun.training.mapper.TrainingOutlineDraftMapper;
import com.yuedongyun.training.mapper.TrainingOutlineMapper;
import com.yuedongyun.training.service.ITrainingOutlineAssessmentDraftService;
import com.yuedongyun.training.service.ITrainingOutlineDraftService;
import com.yuedongyun.training.service.ITrainingOutlineService;
import com.yuedongyun.training.service.ITrainingDraftService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 目录草稿 服务实现类
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-19
 */
@Service
@Slf4j
public class TrainingOutlineDraftServiceImpl extends ServiceImpl<TrainingOutlineDraftMapper, TrainingOutlineDraft> implements ITrainingOutlineDraftService {

    @Autowired
    private TrainingOutlineMapper trainingOutlineMapper;

    @Autowired
    private ITrainingOutlineService trainingOutlineService;

    @Autowired
    private ITrainingDraftService trainingDraftService;

    @Autowired
    private TrainingOutlineAssessmentDraftMapper trainingOutlineAssessmentDraftMapper;

    @Autowired
    private TrainingOutlineAssessmentMapper trainingOutlineAssessmentMapper;

    @Autowired
    private TrainingOutlineDraftMapper trainingOutlineDraftMapper;

    @Autowired
    private ExamClient examClient;

    @Autowired
    private ITrainingOutlineAssessmentDraftService trainingOutlineAssessmentDraftService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void save(Long trainingId, List<OutlineSaveDTO> outlineSaveDTOS, Integer step) {
        //1.根据章的序号按照升序重新排序
        outlineSaveDTOS = outlineSaveDTOS
                .stream()
                .sorted(Comparator.comparing(OutlineSaveDTO::getIndex))
                .collect(Collectors.toList());

        //2.校验章的序号
        if (outlineSaveDTOS.size() != outlineSaveDTOS
                .stream()
                .map(OutlineSaveDTO::getIndex)
                .distinct()
                .count()) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_CATAS_SAVE_INEDX);
        }
        if (outlineSaveDTOS.size() < outlineSaveDTOS.get(outlineSaveDTOS.size() - 1).getIndex()) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_CATAS_SAVE_INEDX_JUMP);
        }

        //2.已经上架的目录
        LambdaQueryWrapper<TrainingOutline> queryWrapper =
                Wrappers.lambdaQuery(TrainingOutline.class)
                        .eq(TrainingOutline::getTrainingId, trainingId);
        List<TrainingOutline> trainingOutlines = trainingOutlineMapper.selectList(queryWrapper);

        //2.1.校验以上架目录是否更新
        checkIndex(outlineSaveDTOS, trainingOutlines);
        //3.组装数据保存到数据库
        List<TrainingOutlineDraft> trainingOutlineDrafts =
                packageOutline(trainingId, outlineSaveDTOS, trainingOutlines);
        //4.删除原有目录信息
        if (step == TrainingConstants.TrainingStep.OUTLINE) {
            //4.1删除小节和章数据
            trainingOutlineDraftMapper.deleteByTrainingId(trainingId,
                    Arrays.asList(
                            TrainingConstants.OutlineType.PHASE,
                            TrainingConstants.OutlineType.SESSION));
        } else if (step == TrainingConstants.TrainingStep.ASSESSMENT) {
            //4.2保存题目时需要删除所有目录
            trainingOutlineDraftMapper.deleteByTrainingId(trainingId,
                    Arrays.asList(
                            TrainingConstants.OutlineType.PHASE,
                            TrainingConstants.OutlineType.SESSION,
                            TrainingConstants.OutlineType.PRATICE));
        } else {
            throw new BizIllegalException(ErrorInfo.Msg.OPERATE_FAILED);
        }
        //5.目录重新插入草稿
        this.saveOrUpdateBatch(trainingOutlineDrafts);

        //6.修改训练编辑进度
        trainingDraftService.updateStep(trainingId, TrainingConstants.TrainingStep.OUTLINE);

        //7.删除已删除章节题目
        trainingOutlineAssessmentDraftService.deleteNotInOutlineIdList(trainingId);
    }

    @Override
    public List<OutlineVO> queryTrainingOutlines(Long trainingId, Boolean see, Boolean withPractice) {
        if (see) {
            //1.1查询正式数据目录
            List<OutlineVO> outlineVOS = trainingOutlineService.queryTrainingOutlinesVO(trainingId, withPractice);
            if (CollUtils.isNotEmpty(outlineVOS)) {
                return outlineVOS;
            }
            //1.2查看草稿目录
            outlineVOS = queryTrainingOutlines(trainingId, withPractice);
            return CollUtils.isEmpty(outlineVOS) ? new ArrayList<>() : outlineVOS;

        } else {
            //2.1查看草稿目录
            List<OutlineVO> outlineVOS = queryTrainingOutlines(trainingId, withPractice);
            return CollUtils.isEmpty(outlineVOS) ? new ArrayList<>() : outlineVOS;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void saveMediaInfo(Long trainingId, List<TrainingMediaDTO> trainingMediaDTOS) {
        //1.校验保存视频的小节id是否属于当前训练中的小节，
        List<Long> outlineIds =
                trainingMediaDTOS.stream()
                        .map(TrainingMediaDTO::getOutlineId)
                        .collect(Collectors.toList());
        //2.每个小节都有上传媒资
        checkSessionIds(outlineIds, trainingId);

        //3.获取训练草稿信息
        TrainingDraft trainingDraft = trainingDraftService.getById(trainingId);
        //3.1判断新增训练是否按照顺序上传媒资
        if (trainingDraft == null ||
                trainingDraft.getStep() < TrainingConstants.TrainingStep.OUTLINE) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_MEDIA_SAVE_NO_EXECUTE);
        }

        //4.媒资设置到小节信息中
        List<TrainingOutlineDraft> outlineDrafts =
                BeanUtils.copyList(trainingMediaDTOS, TrainingOutlineDraft.class,
                        (dto, trainingOutlineDraft) ->
                                trainingOutlineDraft.setId(dto.getOutlineId()));
        //4.1.更新小节媒资信息
        this.updateBatchById(outlineDrafts);
        //4.2.更新训练填写步骤
        trainingDraftService.updateStep(trainingId, TrainingConstants.TrainingStep.MEDIA);
        //5.统计每个章节媒资播放总时长
        List<TrainingOutlineDraft> trainingOutlineDrafts = calculateOutlinelogMediaDuration(trainingId);
        //5.1批量更新每个大章的课时总数量
        this.updateBatchById(trainingOutlineDrafts, 500);


    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void saveSuject(Long trainingId, List<OutlineAssessmentDTO> outlineAssessmentDTOS) {
        //1.数据校验
        //1.1.转化目录id列表
        List<Long> outlineIds = outlineAssessmentDTOS
                .stream()
                .map(OutlineAssessmentDTO::getOutlineId)
                .collect(Collectors.toList());
        checkPracticeIds(outlineIds, trainingId);
        List<TrainingOutlineAssessmentDraft> outlineAssessmentDrafts = new ArrayList<>();

        //2.查询训练草稿
        TrainingDraft trainingDraft = trainingDraftService.getById(trainingId);
        //2.1.判断当前是否可以上传题目
        if (trainingDraft == null || trainingDraft.getStep() < TrainingConstants.TrainingStep.MEDIA) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_MEDIA_SAVE_NO_EXECUTE);
        }

        //3.组装题目目录关系
        for (OutlineAssessmentDTO outlineAssessmentDTO : outlineAssessmentDTOS) {
            for (Long assessmentId : outlineAssessmentDTO.getAssessmentIds()) {
                TrainingOutlineAssessmentDraft trainingOutlineAssessmentDraft = new TrainingOutlineAssessmentDraft();
                //3.1.训练id
                trainingOutlineAssessmentDraft.setTrainingId(trainingId);
                //3.2.题目id
                trainingOutlineAssessmentDraft.setAssessmentId(assessmentId);
                //3.3.训练目录
                trainingOutlineAssessmentDraft.setOutlineId(outlineAssessmentDTO.getOutlineId());
                if (trainingOutlineAssessmentDraft.getId() == null) {
                    trainingOutlineAssessmentDraft.setId(IdWorker.getId());
                }
                //3.4.训练题目关系添加到
                outlineAssessmentDrafts.add(trainingOutlineAssessmentDraft);
            }
        }
        //4.删除练习和题目对应的关系
        trainingOutlineAssessmentDraftMapper.deleteByTrainingId(trainingId);
        //5.批量插入练习和题目之间的关系
        if (!outlineAssessmentDrafts.isEmpty()) {
            trainingOutlineAssessmentDraftMapper.batchInsert(outlineAssessmentDrafts);
        }
        //6.修改训练填写进度
        trainingDraftService.updateStep(trainingId, TrainingConstants.TrainingStep.ASSESSMENT);
    }

    @Override
    public List<OutlineSimpleAssessmentVO> getSuject(Long trainingId) {

        //1.查询训练目录和题目关系
        List<TrainingOutlineAssessmentDraft> outlineAssessmentDrafts = trainingOutlineAssessmentDraftMapper.getByTrainingId(trainingId);
        if (CollUtils.isEmpty(outlineAssessmentDrafts)) {
            return new ArrayList<>();
        }
        List<Long> assessmentIdList = outlineAssessmentDrafts.stream().map(TrainingOutlineAssessmentDraft::getAssessmentId).collect(Collectors.toList());
        List<QuestionDTO> assessments = examClient.queryQuestionByIds(assessmentIdList);
        Map<Long, String> assessmentIdAndNameMap = assessments.stream()
                .collect(Collectors.toMap(QuestionDTO::getId, QuestionDTO::getName));

        //4.组装数据
        return outlineAssessmentDrafts.stream()
                //4.1.分组
                .collect(Collectors.groupingBy(TrainingOutlineAssessmentDraft::getOutlineId))
                .entrySet().stream().map(entry -> {
                    //4.2.小节或测试对应的题目列表
                    List<OutlineSimpleAssessmentVO.AssessmentInfo> assessmentInfos = new ArrayList<>();
                    for (TrainingOutlineAssessmentDraft outlineAssessmentDraft : entry.getValue()) {
                        //4.3.将题目id和题目名称加入到小节或测试的题目列表中
                        assessmentInfos.add(new OutlineSimpleAssessmentVO.AssessmentInfo(
                                outlineAssessmentDraft.getAssessmentId(),
                                assessmentIdAndNameMap.get(outlineAssessmentDraft.getAssessmentId())));
                    }
                    //4.4.组装小节或测试对应题目列表model
                    return new OutlineSimpleAssessmentVO(entry.getKey(), assessmentInfos);
                }).collect(Collectors.toList());
    }

    @Override
    public void checkOutlineInfoImplated(Long trainingId) {
        //查询所有目录
        LambdaQueryWrapper<TrainingOutlineDraft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrainingOutlineDraft::getTrainingId, trainingId);
        List<TrainingOutlineDraft> trainingOutlineDrafts = baseMapper.selectList(queryWrapper);

        List<TrainingOutlineAssessmentDraft> trainingOutlineAssessmentDrafts = trainingOutlineAssessmentDraftMapper.getByTrainingId(trainingId);

        Map<Long, Long> assessmentNumMap = CollUtils.isEmpty(trainingOutlineAssessmentDrafts) ? new HashMap<>() :
                trainingOutlineAssessmentDrafts.stream().collect(
                        Collectors.groupingBy(TrainingOutlineAssessmentDraft::getOutlineId,
                                Collectors.counting()));

        //校验练习或小节有没有上传题目和媒资
        CollUtils.check(trainingOutlineDrafts, new Checker<TrainingOutlineDraft>() {
            @Override
            public void check(TrainingOutlineDraft trainingOutlineDraft) {
                if (trainingOutlineDraft.getType() == TrainingConstants.OutlineType.SESSION
                        && StringUtils.isEmpty(trainingOutlineDraft.getVideoName())) { //小节未上传视频
                    throw new BizIllegalException(
                            StringUtils.format(TrainingErrorInfo.Msg.TRAINING_UP_SHELF_SECTION_WITHOUT_MEDIA, trainingOutlineDraft.getName()));
                } else if (trainingOutlineDraft.getType() == TrainingConstants.OutlineType.PRATICE
                        && NumberUtils.null2Zero(assessmentNumMap.get(trainingOutlineDraft.getId())) <= 0) { //练习未添加题目
                    throw new BizIllegalException(
                            StringUtils.format(TrainingErrorInfo.Msg.TRAINING_UP_SHELF_PRACTICE_WITHOUT_SUBJECT,
                                    trainingOutlineDraft.getName())
                    );
                }
            }
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void copyAssessmentToShelf(Long trainingId, Boolean isFirstShelf) {
        //1.从草稿中查出题目信息
        List<TrainingOutlineAssessmentDraft> trainingOutlineAssessmentDrafts = trainingOutlineAssessmentDraftMapper.getByTrainingId(trainingId);
        List<QuestionBizDTO> assessments = trainingOutlineAssessmentDrafts.stream()
                .map(s -> QuestionBizDTO.of(s.getOutlineId(), s.getAssessmentId())).collect(Collectors.toList());
        //2.删除练习和题目之间的关系
        if (CollUtils.isEmpty(trainingOutlineAssessmentDrafts)) {
            // 草稿中没有题目，直接结束
            return;
        }
        //3.将新的练习和题目之间的关系上架
        examClient.saveQuestionBizInfoBatch(assessments);
        //4.删除草稿
        int result = trainingOutlineAssessmentDraftMapper.deleteByTrainingId(trainingId);
        if (result != trainingOutlineAssessmentDrafts.size()) {
            throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void copyToShelf(Long trainingId, Boolean isFirstShelf) {
        //1.从草稿中查出目录信息
        List<TrainingOutlineDraft> trainingOutlineDrafts = baseMapper.getByTrainingId(trainingId);
        List<TrainingOutline> trainingOutlines = BeanUtils.copyList(trainingOutlineDrafts, TrainingOutline.class);
        //2.保存数据上架
        boolean result = trainingOutlineService.saveOrUpdateBatch(trainingOutlines);
        if (!result) {
            throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
        }
        //3.删除草稿
        int deleteResult = trainingOutlineDraftMapper.deleteByTrainingId(trainingId, Arrays.asList(
                TrainingConstants.OutlineType.PHASE,
                TrainingConstants.OutlineType.SESSION,
                TrainingConstants.OutlineType.PRATICE
        ));
        if (deleteResult != trainingOutlineDrafts.size()) {
            throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
        }
    }

    @Override
    public Map<Long, Integer> calculateMediaDuration(Long trainingId) {
        LambdaQueryWrapper<TrainingOutlineDraft> queryWrapper =
                Wrappers.lambdaQuery(TrainingOutlineDraft.class)
                        .eq(TrainingOutlineDraft::getTrainingId, trainingId)
                        .eq(TrainingOutlineDraft::getType, TrainingConstants.OutlineType.SESSION);
        List<TrainingOutlineDraft> list = list(queryWrapper);
        if (CollUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream()
                .collect(Collectors.groupingBy(
                        TrainingOutlineDraft::getParentOutlineId,
                        Collectors.summingInt(TrainingOutlineDraft::getMediaDuration)));
    }

    @Override
    public Integer totalSessionNums(Long trainingId) {
        LambdaQueryWrapper<TrainingOutlineDraft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrainingOutlineDraft::getTrainingId, trainingId)
                .in(TrainingOutlineDraft::getType,
                        Arrays.asList(TrainingConstants.OutlineType.SESSION, TrainingConstants.OutlineType.PRATICE));
        return count(queryWrapper);
    }

    @Override
    public List<Long> queryOutlineIdsOfTraining(Long trainingId, List<Integer> types) {
        //1.查询条件
        LambdaQueryWrapper<TrainingOutlineDraft> queryWrapper =
                Wrappers.lambdaQuery(TrainingOutlineDraft.class)
                        .eq(TrainingOutlineDraft::getTrainingId, trainingId)
                        .in(TrainingOutlineDraft::getType, types);
        //2.查询数据
        List<TrainingOutlineDraft> trainingOutlineDrafts = baseMapper.selectList(queryWrapper);
        //3.返回数据
        return CollUtils.isEmpty(trainingOutlineDrafts)
                ? new ArrayList<>()
                : trainingOutlineDrafts
                .stream()
                .map(TrainingOutlineDraft::getId)
                .collect(Collectors.toList());
    }


    /**
     * 校验已经上架训练的章节是否被调动或删除
     *
     * @param trainingOutlines 已上架的
     * @param outlineSaveDTOS     前端传来要保存的目录列表
     */
    private void checkIndex(List<OutlineSaveDTO> outlineSaveDTOS, List<TrainingOutline> trainingOutlines) {

        //校验章序号是否有重复的
        Map<Integer, OutlineSaveDTO> collect = outlineSaveDTOS.stream().collect(Collectors.toMap(OutlineSaveDTO::getIndex, p -> p));
        if (collect.size() < outlineSaveDTOS.size()) { //有重复的章序号
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_CATAS_SAVE_CHAPTER_INDEX_REPEAT);
        }
        //outlineSaveDTOS 是按升序排的 最大的章序号大于章数量，说明章列表outlineSaveDTOS的序号有间断设置
        if (outlineSaveDTOS.get(outlineSaveDTOS.size() - 1).getIndex() > outlineSaveDTOS.size()) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_CATAS_SAVE_CHAPTER_INDEX_INTERRUPTED);
        }

        if (CollUtils.isEmpty(trainingOutlines)) {
            return;
        }
        final Map<Long, Integer> saveIndexMap = new HashMap<>();
        for (OutlineSaveDTO outlineSaveDTO : outlineSaveDTOS) {//章的序号
            if (outlineSaveDTO.getId() != null) {
                saveIndexMap.put(outlineSaveDTO.getId(), outlineSaveDTO.getIndex());
            }

            //小节和练习的序号
            if (CollUtils.isEmpty(outlineSaveDTO.getSessions())) { //章没有对应的小节
                throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_CATAS_SAVE_CHAPTER_WITHOUT_SECTION);
            }
            AtomicInteger count = new AtomicInteger(1);
            //小节需要排序，练习不需要排序
            outlineSaveDTO.getSessions().stream().filter(session -> session.getType() == TrainingConstants.OutlineType.SESSION)
                    .forEach(session -> {
                        if (session.getId() == null) {
                            //序号从1开始
                            saveIndexMap.put(session.getId(), count.incrementAndGet());
                        }
                    });
        }

        for (TrainingOutline trainingOutline : trainingOutlines) {
            if (trainingOutline.getType() != TrainingConstants.OutlineType.PHASE) {
                continue;
            }
            Integer index = saveIndexMap.get(trainingOutline.getId());
            if (index == null) {
                throw new BizIllegalException(StringUtils.format(TrainingErrorInfo.Msg.TRAINING_CATAS_SAVE_CHAPTER_NAME_DELETED, trainingOutline.getName()));
            }
            //章排序
            if (!index.equals(trainingOutline.getCIndex())) {
                throw new BizIllegalException(StringUtils.format(ErrorInfo.Msg.OPERATE_FAILED, trainingOutline.getName()));
            }
        }
    }

    /**
     * 组装数据,数据优先级，本次保存数据>草稿中的数据>已上架数据
     * 通过map来实现数据的覆盖，map key的目录id，value是目录信息
     * 1.先将已上架的数据放入map
     * 2.将草稿中的数据放入map，放入的过程中草稿中和已上架都存在，草稿中的数据会自动覆盖掉已上架数据
     * 3.遍历本次保存数据，
     * 3.1 如果目录信息没有id，生成一个
     * 3.2 在map中获取目录信息，没有找到生成一个目录草稿类
     * 3.3 向目录信息中添加要保存的数据 序号、名称、小节id、目录id
     *
     * @param trainingId     训练id
     * @param outlineSaveDTOS 训练目录列表
     * @return 数据库中存储的训练目录列表
     */
    private List<TrainingOutlineDraft> packageOutline(Long trainingId, List<OutlineSaveDTO> outlineSaveDTOS, List<TrainingOutline> shelfTrainingOutlines) {
        final Map<Long, TrainingOutlineDraft> savedMap = new HashMap<>();
        if (CollUtils.isNotEmpty(shelfTrainingOutlines)) {
            //将已经上架的目录copy下来
            shelfTrainingOutlines.stream().forEach(trainingOutline ->
                    savedMap.put(trainingOutline.getId(), BeanUtils.toBean(trainingOutline, TrainingOutlineDraft.class)));
        }
        //获取已经保存的草稿
        LambdaQueryWrapper<TrainingOutlineDraft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrainingOutlineDraft::getTrainingId, trainingId);
        List<TrainingOutlineDraft> savedTrainingOutlinesDraft = list(queryWrapper);
        if (CollUtils.isNotEmpty(savedTrainingOutlinesDraft)) {
            //存在草稿覆盖已经上架的训练
            savedTrainingOutlinesDraft.stream().forEach(trainingOutlineDraft ->
                    savedMap.put(trainingOutlineDraft.getId(), trainingOutlineDraft));
        }
        //需要保存的草稿
        List<TrainingOutlineDraft> trainingOutlineDrafts = new ArrayList<>();
        for (OutlineSaveDTO outlineSaveDTO : outlineSaveDTOS) {
            Long phaseId = outlineSaveDTO.getId() == null ? IdWorker.getId() : outlineSaveDTO.getId();
            //章目录
            TrainingOutlineDraft trainingOutlineDraft = savedMap.get(phaseId);
            if (trainingOutlineDraft == null) { //未保存过
                trainingOutlineDraft = new TrainingOutlineDraft();
                trainingOutlineDraft.setId(phaseId);
            }

            //设置目录添加或修改时的基本信息
            trainingOutlineDraft.setOutlineBaseInfo(outlineSaveDTO.getIndex(), outlineSaveDTO.getName(), TrainingConstants.OutlineType.PHASE,
                    0L, trainingId);
            trainingOutlineDrafts.add(trainingOutlineDraft);
            //小节练习目录，小节有需要，练习没有需要
            //序号
            AtomicInteger indexCount = new AtomicInteger(0);
            outlineSaveDTO.getSessions().stream().forEach(session -> {
                Long sessionId = session.getId() == null ? IdWorker.getId() : session.getId();
                //小节练习目录
                TrainingOutlineDraft trainingOutlineDraftSession = savedMap.get(sessionId);
                if (trainingOutlineDraftSession == null) { //未保存过
                    trainingOutlineDraftSession = new TrainingOutlineDraft();
                    trainingOutlineDraftSession.setId(sessionId);
                }
                Integer index = session.getType() == TrainingConstants.OutlineType.SESSION ?
                        indexCount.incrementAndGet() : null;

                //设置目录添加或修改时的基本信息
                trainingOutlineDraftSession.setOutlineBaseInfo(index, session.getName(), session.getType(),
                        phaseId, trainingId);
                trainingOutlineDrafts.add(trainingOutlineDraftSession);
            });
        }
        return trainingOutlineDrafts;
    }

    private List<OutlineVO> queryTrainingOutlines(Long trainingId, Boolean withPractice) {
        LambdaQueryWrapper<TrainingOutlineDraft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrainingOutlineDraft::getTrainingId, trainingId);
        if (!withPractice) {
            queryWrapper.in(TrainingOutlineDraft::getType,
                    Arrays.asList(TrainingConstants.OutlineType.PHASE, TrainingConstants.OutlineType.SESSION));
        }
        //根据类型和排序，按照
        queryWrapper.last(" order by type,c_index");
        List<TrainingOutlineDraft> trainingOutlineDrafts = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(trainingOutlineDrafts)) {
            return null;
        }
        TrainingDraft trainingDraft = trainingDraftService.getById(trainingId);

        // 最大上架数,待上架设置空map，已上架需要排序并去小节序号（同一个章中）中最大小节
        Map<Long, TrainingOutlineDraft> phaseIdAndMaxSessionMap =
                (trainingDraft.getStatus() == TrainingStatus.NO_UP_SHELF.getStatus())
                        ? new HashMap<>() :
                        trainingOutlineDrafts.parallelStream()
                                .filter(ccd -> ccd.getType() == TrainingConstants.OutlineType.SESSION && !ccd.getCanUpdate())
                                .collect(Collectors.groupingBy(TrainingOutlineDraft::getParentOutlineId,
                                        Collectors.collectingAndThen(
                                                Collectors.reducing(
                                                        (c1, c2) -> c2.getCIndex().compareTo(c1.getCIndex()) > 0 ? c2 : c1),
                                                Optional::get)));
        int maxPhaseIndex = (trainingDraft.getStatus() == TrainingStatus.NO_UP_SHELF.getStatus())
                ? 0
                : trainingOutlineDrafts.stream()
                .filter(ccd -> ccd.getType() == TrainingConstants.OutlineType.PHASE && !ccd.getCanUpdate())
                .map(TrainingOutlineDraft::getCIndex)
                .max(Integer::compare).get();


        // 4.查询训练对应的小节和题目信息
        List<TrainingOutlineAssessmentDraft> assessments = trainingOutlineAssessmentDraftMapper.getByTrainingId(trainingId);
        // 4.1.统计题目数量
        Map<Long, Long> outlineIdAndNumMap = CollUtils.isEmpty(assessments) ? new HashMap<>() :
                assessments.stream().collect(Collectors.groupingBy(TrainingOutlineAssessmentDraft::getOutlineId, Collectors.counting()));
        // 4.2.查询分数
        Map<Long, Integer> outlineIdAndTotalScoreMap = new HashMap<>(outlineIdAndNumMap.size());
        if (CollUtils.isNotEmpty(assessments)) {
            Set<Long> questionIds = assessments.stream().map(TrainingOutlineAssessmentDraft::getAssessmentId).collect(Collectors.toSet());
            Map<Long, Integer> scoreMap = examClient.queryQuestionScores(questionIds);
            outlineIdAndTotalScoreMap.putAll(
                    assessments.stream().collect(Collectors.groupingBy(
                            TrainingOutlineAssessmentDraft::getOutlineId,
                            Collectors.summingInt(d -> scoreMap.get(d.getAssessmentId()))
                    )));
        }
        return TreeDataUtils.parseToTree(trainingOutlineDrafts, OutlineVO.class, (outlineDraft, vo) -> {
            int maxIndexOnShelf = 0;
            int maxSessionIndexOnShelf = 0;
            if (outlineDraft.getType() == TrainingConstants.OutlineType.SESSION) {
                //小节最大编辑数
                TrainingOutlineDraft trainingOutlineDraft = phaseIdAndMaxSessionMap.get(outlineDraft.getParentOutlineId());
                maxIndexOnShelf = NumberUtils.null2Zero(
                        trainingOutlineDraft == null ? 0 : trainingOutlineDraft.getCIndex());
            } else if (outlineDraft.getType() == TrainingConstants.OutlineType.PHASE) {
                maxIndexOnShelf = maxPhaseIndex;
                TrainingOutlineDraft trainingOutlineDraft = phaseIdAndMaxSessionMap.get(outlineDraft.getId());
                maxSessionIndexOnShelf = NumberUtils.null2Zero(
                        trainingOutlineDraft == null ? 0 : trainingOutlineDraft.getCIndex());
            }
            vo.setIndex(outlineDraft.getCIndex());
            vo.setMediaName(outlineDraft.getVideoName());
            vo.setAssessmentNum(NumberUtils.null2Zero(outlineIdAndNumMap.get(outlineDraft.getId())).intValue()); //练习总数量
            vo.setTotalScore(NumberUtils.null2Zero(outlineIdAndTotalScoreMap.get(outlineDraft.getId()))); //练习总分数
            vo.setMaxIndexOnShelf(maxIndexOnShelf);
            vo.setMaxSessionIndexOnShelf(maxSessionIndexOnShelf);
        }, new TrainingOutlinelogDraftDataWrapper());
    }

    /**
     * 校验练习id列表
     * 1.校验本训练所有的练习都添加了题目
     * 2.校验上传的数据中有其他训练的数据，
     * 前端传过来的章节id列表为A，训练所有小节id和练习id列表为B，训练所有练习id列表为c
     * 原理：1. 判断A是不是B的子集，如果A不是B的子集，则前端传过来的id列表中有非当前训练的小节或练习
     * 2.判断c是A的子集，如果c不是A的子集，说明前端有漏传的该训练的练习id
     *
     * @param outlineIds  前端传过来的小节id列表或者练习列表
     * @param trainingId 训练id
     */
    private void checkPracticeIds(List<Long> outlineIds, Long trainingId) {
        //查询所有小节和练习的目录列表
        LambdaQueryWrapper<TrainingOutlineDraft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrainingOutlineDraft::getTrainingId, trainingId).in(TrainingOutlineDraft::getType,
                Arrays.asList(TrainingConstants.OutlineType.SESSION, TrainingConstants.OutlineType.PRATICE));
        List<TrainingOutlineDraft> trainingOutlineDrafts = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(trainingOutlineDrafts)) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_CATA_NOT_EXISTS);
        }
        //所有小节和练习的目录id列表
        List<Long> allOutlineIdList = trainingOutlineDrafts
                .stream()
                .map(TrainingOutlineDraft::getId)
                .collect(Collectors.toList());

        //判断前端传过来的章节id列表是该训练所有小节和练习集合的子集，
        // 如果不是子集，说明前端传过来的章节id不属于当前训练
        if (!CollUtils.containsAll(allOutlineIdList, outlineIds)) {
            log.error("传过来了其他章节的章节id，trainingId:{},outlineIds:{}", trainingId, outlineIds);
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_MEDIA_SAVE_ILLEGAL);
        }
        //所有练习的id列表
        List<Long> practiceIdList = trainingOutlineDrafts.stream().filter(practice -> practice.getType() == TrainingConstants.OutlineType.PRATICE).
                map(TrainingOutlineDraft::getId).collect(Collectors.toList());
        //判断前端传过来的章节id列表是否符合是训练练习id父集合
        if (!CollUtils.containsAll(outlineIds, practiceIdList)) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_SUBJECT_SAVE_SUBJECT_IDS_NULL);
        }
    }

    /**
     * 校验小节或练习id列表
     * 1.校验本训练所有的小节都添加了视频
     * 2.校验上传的数据中有其他训练的数据，
     * 原理：1.先比较接口传递过来的数据和数据库中的小节总数量一致,不一致返回失败
     * 2.总数量一致，再求两者的交集，交集集合长度和前端传过来的数量一致，则通过
     *
     * @param outlineIds  前端传过来的小节id列表或者练习列表
     * @param trainingId 训练id
     */
    private void checkSessionIds(List<Long> outlineIds, Long trainingId) {
        //1.数据库小节查询条件
        LambdaQueryWrapper<TrainingOutlineDraft> queryWrapper =
                Wrappers.lambdaQuery(TrainingOutlineDraft.class)
                        .eq(TrainingOutlineDraft::getType, TrainingConstants.OutlineType.SESSION)
                        .eq(TrainingOutlineDraft::getTrainingId, trainingId);
        //2.查询小节
        List<TrainingOutlineDraft> trainingOutlineDrafts = baseMapper.selectList(queryWrapper);

        //3.判断数据库中的小节和前端传来的小节数量是否一致
        if (CollUtils.size(trainingOutlineDrafts) != CollUtils.size(outlineIds)) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_MEDIA_SAVE_MEDIA_NULL);
        }
        //4.转化出数据库中训练小节id列表
        List<Long> outlineIdsInDb =
                trainingOutlineDrafts
                        .stream()
                        .map(TrainingOutlineDraft::getId)
                        .collect(Collectors.toList());
        //5.取前端传来小节id列表和数据库中小节id列表交集
        Collection<Long> outlineIdsOfIntersession = CollUtils.intersession(outlineIds, outlineIdsInDb);
        //6.判断小节交集id列表数量和前端传来小节id列表数量是否一致
        if (outlineIdsOfIntersession.size() != outlineIds.size()) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.TRAINING_MEDIA_SAVE_MEDIA_NULL);
        }
    }

    /**
     * 根据训练id统计每个大章的媒资总时长
     *
     * @param trainingId
     * @return
     */
    private List<TrainingOutlineDraft> calculateOutlinelogMediaDuration(Long trainingId) {
        //1.查询条件
        LambdaQueryWrapper<TrainingOutlineDraft> queryWrapper =
                Wrappers.lambdaQuery(TrainingOutlineDraft.class)
                        .eq(TrainingOutlineDraft::getTrainingId, trainingId)
                        .eq(TrainingOutlineDraft::getType, TrainingConstants.OutlineType.SESSION);
        //2.查询数据
        List<TrainingOutlineDraft> trainingOutlineDrafts = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(trainingOutlineDrafts)) {
            return new ArrayList<>();
        }
        //3.统计每个章的课时总时长
        Map<Long, Integer> capthIdAndMediaDurationMap =
                trainingOutlineDrafts.stream().collect(Collectors.groupingBy(
                        TrainingOutlineDraft::getParentOutlineId,
                        Collectors.summingInt(TrainingOutlineDraft::getMediaDuration)));
        //4.封装数据
        return capthIdAndMediaDurationMap.keySet()
                .stream()
                .map(key -> TrainingOutlineDraft.builder()
                        .id(key)
                        .mediaDuration(capthIdAndMediaDurationMap.get(key))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 训练目录草稿树型数据转换器
     */
    private class TrainingOutlinelogDraftDataWrapper implements TreeDataUtils.DataProcessor<OutlineVO, TrainingOutlineDraft> {

        @Override
        public Object getParentKey(TrainingOutlineDraft trainingOutlineDraft) {
            return trainingOutlineDraft.getParentOutlineId();
        }

        @Override
        public Object getKey(TrainingOutlineDraft trainingOutlineDraft) {
            return trainingOutlineDraft.getId();
        }

        @Override
        public Object getRootKey() {
            return 0L;
        }

        @Override
        public List<OutlineVO> getChild(OutlineVO outlineVO) {
            return outlineVO.getSessions();
        }

        @Override
        public void setChild(OutlineVO parent, List<OutlineVO> child) {
            parent.setSessions(child);
        }
    }
}

