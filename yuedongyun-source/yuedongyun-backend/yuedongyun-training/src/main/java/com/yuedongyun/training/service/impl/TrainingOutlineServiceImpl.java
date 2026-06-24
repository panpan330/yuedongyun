package com.yuedongyun.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.api.client.exam.ExamClient;
import com.yuedongyun.api.dto.training.OutlineDTO;
import com.yuedongyun.api.dto.training.MediaQuoteDTO;
import com.yuedongyun.api.dto.training.SessionInfoDTO;
import com.yuedongyun.api.dto.exam.QuestionBizDTO;
import com.yuedongyun.common.exceptions.BizIllegalException;
import com.yuedongyun.common.utils.*;
import com.yuedongyun.training.constants.TrainingConstants;
import com.yuedongyun.training.constants.TrainingErrorInfo;
import com.yuedongyun.training.domain.po.TrainingOutline;
import com.yuedongyun.training.domain.vo.OutlineSimpleInfoVO;
import com.yuedongyun.training.domain.vo.OutlineVO;
import com.yuedongyun.training.mapper.TrainingOutlineMapper;
import com.yuedongyun.training.properties.TrainingProperties;
import com.yuedongyun.training.service.ITrainingOutlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
public class TrainingOutlineServiceImpl extends ServiceImpl<TrainingOutlineMapper, TrainingOutline> implements ITrainingOutlineService {

    @Autowired
    private TrainingProperties trainingProperties;

    @Autowired
    private ExamClient examClient;

    @Override
    public List<OutlineDTO> queryTrainingOutlines(Long trainingId, Boolean withPractice) {
        //1.训练目录查询条件
        LambdaQueryWrapper<TrainingOutline> queryWrapper =
                Wrappers.lambdaQuery(TrainingOutline.class)
                        .eq(TrainingOutline::getTrainingId, trainingId);
        if (!withPractice) {
            //1.1训练目录不带练习设置目录查询类型
            queryWrapper.in(TrainingOutline::getType,
                    Arrays.asList(TrainingConstants.OutlineType.SESSION,
                            TrainingConstants.OutlineType.PHASE));
        }
        //1.2根据目录类型和序号排序
        queryWrapper.last(" order by type,c_index");
        //2.查询训练目录列表
        List<TrainingOutline> trainingOutlines = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(trainingOutlines)) {
            return null;
        }
        //3.查询训练目录对应题目数量
        Set<Long> ids = trainingOutlines.stream().map(TrainingOutline::getId).collect(Collectors.toSet());
        List<QuestionBizDTO> questionBizDTOS = examClient.queryQuestionIdsByBizIds(ids);
        //4.转化目录id和题目id、分数对应关系
        Map<Long, Long> outlineIdAndNumMap =
                CollUtils.isEmpty(questionBizDTOS)
                        ? new HashMap<>() :
                        questionBizDTOS
                                .stream()
                                .collect(Collectors.groupingBy(QuestionBizDTO::getBizId, Collectors.counting()));
        // 5.组织树结构并返回
        return TreeDataUtils.parseToTree(trainingOutlines, OutlineDTO.class, (trainingOutline, outlineVO)->{
            outlineVO.setMediaName(trainingOutline.getVideoName());
            outlineVO.setIndex(trainingOutline.getCIndex());
            outlineVO.setAssessmentNum(outlineIdAndNumMap.getOrDefault(trainingOutline.getId(), 0L).intValue());
        }, new TrainingOutlinelogDataWrapper());
    }

    @Override
    public List<MediaQuoteDTO> countMediaUserInfo(List<Long> mediaIds) {
        //1.判断媒资id列表为空判断
        if (CollUtils.isEmpty(mediaIds)) {
            return CollUtils.emptyList();
        }
        //2.训练目录查询条件
        LambdaQueryWrapper<TrainingOutline> queryWrapper =
                Wrappers.lambdaQuery(TrainingOutline.class)
                .in(TrainingOutline::getMediaId, mediaIds);
        //2.1查询训练目录列表
        List<TrainingOutline> trainingOutlines = baseMapper.selectList(queryWrapper);
        //3.未查询到媒资对应的训练目录
        if (CollUtils.isEmpty(trainingOutlines)) {
            //3.1媒资都未被引用，都给一个引用次数0
            return mediaIds.stream()
                    .map(mediaId -> new MediaQuoteDTO(mediaId, 0))
                    .collect(Collectors.toList());
        }
        //4.分组统计媒资被引用次数
        Map<Long, Long> mediaAndCount =
                trainingOutlines.stream()
                .collect(Collectors.groupingBy(
                        TrainingOutline::getMediaId, Collectors.counting()));
        //5.组装数据，设置
        return mediaIds.stream().map(
                mediaId -> new MediaQuoteDTO(mediaId,
                        NumberUtils.null2Zero(mediaAndCount.get(mediaId))
                                .intValue())
        ).collect(Collectors.toList());
    }

    @Override
    public SessionInfoDTO getSimpleSessionInfo(Long sessionId) {
        //1.小节id判空
        if (sessionId == null) {
            throw new BizIllegalException(TrainingErrorInfo.Msg.CATEGORY_QUERY_ID_NULL);
        }
        //2.获取小节对应的目录信息
        TrainingOutline trainingOutline = baseMapper.selectById(sessionId);
        if (trainingOutline == null) {
            return new SessionInfoDTO();
        }
        //3.判断目录类型是否为小节
        if (trainingOutline.getType() != TrainingConstants.OutlineType.SESSION) {
            return new SessionInfoDTO();
        }
        //4.组装数据
        SessionInfoDTO sessionInfoDTO = BeanUtils.toBean(trainingOutline, SessionInfoDTO.class);
        //5.设置免费试看时长
        sessionInfoDTO.setFreeDuration(trainingOutline.getTrailer() == 1 ?
                trainingProperties.getMedia().getTrailerDuration() : 0);
        return sessionInfoDTO;
    }

    @Override
    public List<OutlineSimpleInfoVO> getOutlinesIndexList(Long trainingId) {
        //1.训练目录（不含练习）查询条件
        LambdaQueryWrapper<TrainingOutline> queryWrapper =
                Wrappers.lambdaQuery(TrainingOutline.class)
                        .eq(TrainingOutline::getTrainingId, trainingId)
                        .in(TrainingOutline::getType, Arrays.asList(
                                TrainingConstants.OutlineType.PHASE,
                                TrainingConstants.OutlineType.SESSION
                        ));
        //1.1查询训练目录
        List<TrainingOutline> trainingOutlines = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(trainingOutlines)) {
            return new ArrayList<>();
        }
        //2.章id与章序号映射关系
        Map<Long, Integer> phaseMap =
                trainingOutlines
                        .stream()
                        .filter(trainingOutline -> trainingOutline.getType() == TrainingConstants.OutlineType.PHASE)
                        .collect(Collectors.toMap(TrainingOutline::getId, TrainingOutline::getCIndex));
        //3.遍历训练目录，组装数据
        return trainingOutlines.stream()
                .filter(trainingOutline -> trainingOutline.getType() != TrainingConstants.OutlineType.PHASE)

                .map(trainingOutline -> {
                    //3.1组装目录序号
                    String index = StringUtils.format("{}-{}",
                            phaseMap.get(trainingOutline.getParentOutlineId()),
                            trainingOutline.getCIndex());
                    //3.2组装目录信息，目录id，目录名称，目录序号
                    return new OutlineSimpleInfoVO(trainingOutline.getId(),
                            trainingOutline.getName(), index, trainingOutline.getCIndex(), null);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<OutlineSimpleInfoVO> getManyOutlineSimpleInfo(List<Long> ids) {
        // 1.判空
        if(CollUtils.isEmpty(ids)){
            return CollUtils.emptyList();
        }
        //2.查询条件
        LambdaQueryWrapper<TrainingOutline> queryWrapper =
                Wrappers.lambdaQuery(TrainingOutline.class)
                .in(TrainingOutline::getId, ids);
        //3.查询数据
        List<TrainingOutline> trainingOutlines = baseMapper.selectList(queryWrapper);
        //4.数据转化
        return BeanUtils.copyList(trainingOutlines, OutlineSimpleInfoVO.class);
    }

    @Override
    public OutlineSimpleInfoVO querySessionInfoById(Long id) {
        // 1.查询数据
        TrainingOutline currentTrainingOutline = baseMapper.selectById(id);
        // 2.判断是否是小节
        if(currentTrainingOutline != null
                && currentTrainingOutline.getType() == TrainingConstants.OutlineType.SESSION) {
            // 2.1.转换数据
            OutlineSimpleInfoVO outlineSimpleInfoVO = BeanUtils.toBean(currentTrainingOutline, OutlineSimpleInfoVO.class);
            // 3.查询章信息
            TrainingOutline trainingOutline = baseMapper.selectById(currentTrainingOutline.getParentOutlineId());
            // 3.1.章id
            outlineSimpleInfoVO.setPhaseIndex(trainingOutline.getCIndex());
            return outlineSimpleInfoVO;
        }
        // 4.返回空数据
        return new OutlineSimpleInfoVO();
    }

    @Override
    public List<OutlineVO> queryTrainingOutlinesVO(Long trainingId, Boolean withPractice) {
        //1.训练目录查询条件
        LambdaQueryWrapper<TrainingOutline> queryWrapper =
                Wrappers.lambdaQuery(TrainingOutline.class)
                        .eq(TrainingOutline::getTrainingId, trainingId);
        if (!withPractice) {
            //1.1训练目录不带练习设置目录查询类型
            queryWrapper.in(TrainingOutline::getType,
                    Arrays.asList(TrainingConstants.OutlineType.SESSION,
                            TrainingConstants.OutlineType.PHASE));
        }
        //1.2根据目录类型和序号排序
        queryWrapper.last(" order by type,c_index");
        //2.查询训练目录列表
        List<TrainingOutline> trainingOutlines = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(trainingOutlines)) {
            return null;
        }

        //3.查询训练目录id、题目id及分数列表
        Set<Long> ids = trainingOutlines.stream().map(TrainingOutline::getId).collect(Collectors.toSet());
        List<QuestionBizDTO> questionBizDTOS = examClient.queryQuestionIdsByBizIds(ids);
        //4.转化目录id和题目id、分数对应关系
        Map<Long, Long> outlineIdAndNumMap =
                CollUtils.isEmpty(questionBizDTOS)
                        ? new HashMap<>() :
                        questionBizDTOS
                                .stream()
                                .collect(Collectors.groupingBy(QuestionBizDTO::getBizId, Collectors.counting()));
        //5.转化录id和题目id、总分数关系
        Map<Long, Integer> outlineIdAndTotalScoreMap = examClient.queryQuestionScoresByBizIds(ids);
        //6.数据目录结构转化
        List<OutlineVO> outlineVOS =
                TreeDataUtils.parseToTree(trainingOutlines, OutlineVO.class,
                        (trainingOutline, outlineVO) -> {
                            //6.1设置媒资名称
                            outlineVO.setMediaName(trainingOutline.getVideoName());
                            //6.2设置目录索引
                            outlineVO.setIndex(trainingOutline.getCIndex());
                            //6.3设置题目数量
                            outlineVO.setAssessmentNum(NumberUtils.null2Zero(
                                    outlineIdAndNumMap.get(trainingOutline.getId()))
                                    .intValue()); //练习总数量
                            //6.4设置题目总分数
                            outlineVO.setTotalScore(NumberUtils.null2Zero(
                                    outlineIdAndTotalScoreMap.get(trainingOutline.getId()))); //练习总分数
                        }, new TrainingOutlinelogDataWrapper2());

        return outlineVOS;
    }

    //训练目录树形转化模型
    private static class TrainingOutlinelogDataWrapper implements TreeDataUtils.DataProcessor<OutlineDTO, TrainingOutline> {


        @Override
        public Object getParentKey(TrainingOutline trainingOutline) {
            return trainingOutline.getParentOutlineId();
        }

        @Override
        public Object getKey(TrainingOutline trainingOutline) {
            return trainingOutline.getId();
        }

        @Override
        public Object getRootKey() {
            return 0L;
        }

        @Override
        public List<OutlineDTO> getChild(OutlineDTO outlineDTO) {
            return outlineDTO.getSessions();
        }

        @Override
        public void setChild(OutlineDTO parent, List<OutlineDTO> child) {
            parent.setSessions(child);
        }
    }

    //训练目录树形转化模型
    private static class TrainingOutlinelogDataWrapper2 implements TreeDataUtils.DataProcessor<OutlineVO, TrainingOutline> {


        @Override
        public Object getParentKey(TrainingOutline trainingOutline) {
            return trainingOutline.getParentOutlineId();
        }

        @Override
        public Object getKey(TrainingOutline trainingOutline) {
            return trainingOutline.getId();
        }

        @Override
        public Object getRootKey() {
            return 0L;
        }

        @Override
        public List<OutlineVO> getChild(OutlineVO outlineDTO) {
            return outlineDTO.getSessions();
        }

        @Override
        public void setChild(OutlineVO parent, List<OutlineVO> child) {
            parent.setSessions(child);
        }
    }
}

