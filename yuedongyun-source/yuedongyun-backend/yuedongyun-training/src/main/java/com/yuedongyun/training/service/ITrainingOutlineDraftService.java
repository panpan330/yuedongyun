package com.yuedongyun.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.training.domain.dto.OutlineSaveDTO;
import com.yuedongyun.training.domain.dto.OutlineAssessmentDTO;
import com.yuedongyun.training.domain.dto.TrainingMediaDTO;
import com.yuedongyun.training.domain.po.TrainingOutlineDraft;
import com.yuedongyun.training.domain.vo.OutlineSimpleAssessmentVO;
import com.yuedongyun.training.domain.vo.OutlineVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 目录草稿 服务类
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-19
 */
public interface ITrainingOutlineDraftService extends IService<TrainingOutlineDraft> {

    /**
     * 保存训练对应的目录结构
     *
     * @param trainingId 训练id
     * @param outlineSaveDTOS 训练目录信息
     */
    void save(Long trainingId, List<OutlineSaveDTO> outlineSaveDTOS, Integer step);

    /**
     * 查询训练目录
     *
     * @param trainingId 训练id必填
     * @param see      是否用于查看数据
     * @return
     */
    List<OutlineVO> queryTrainingOutlines(Long trainingId, Boolean see, Boolean withPractice);

    /**
     * 保存媒资信息
     */
    void saveMediaInfo(Long trainingId, List<TrainingMediaDTO> trainingMediaDTOS);

    void saveSuject(Long trainingId, List<OutlineAssessmentDTO> outlineAssessmentDTOS);

    /**
     * 根据训练id，获取题目，用于草稿编辑
     *
     * @param trainingId
     * @return
     */
    List<OutlineSimpleAssessmentVO> getSuject(Long trainingId);

    /**
     * 校验训练对应的目录数据完整，包含视频、题目
     *
     * @param trainingId
     */
    void checkOutlineInfoImplated(Long trainingId);

    /**
     * 复制试题到架上
     *
     * @param trainingId
     * @param isFirstShelf
     */
    void copyAssessmentToShelf(Long trainingId, Boolean isFirstShelf);

    /**
     * copy目录到架上
     *
     * @param trainingId
     * @param isFirstShelf
     */
    void copyToShelf(Long trainingId, Boolean isFirstShelf);

    /**
     * 计算当前训练中每个章媒资时长
     *
     * @param trainingId
     * @return
     */
    Map<Long, Integer> calculateMediaDuration(Long trainingId);

    /**
     * 训练总节和练习数，不包含章
     *
     * @param trainingId
     * @return
     */
    Integer totalSessionNums(Long trainingId);

    /**
     * 根据类型查询训练小节/章/考核id列表
     *
     * @param trainingId
     * @param types
     * @return
     */
    List<Long> queryOutlineIdsOfTraining(Long trainingId, List<Integer> types);

}

