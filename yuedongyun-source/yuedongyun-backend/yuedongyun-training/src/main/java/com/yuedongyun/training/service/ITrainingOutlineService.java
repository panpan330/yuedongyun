package com.yuedongyun.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.api.dto.training.OutlineDTO;
import com.yuedongyun.api.dto.training.MediaQuoteDTO;
import com.yuedongyun.api.dto.training.SessionInfoDTO;
import com.yuedongyun.training.domain.po.TrainingOutline;
import com.yuedongyun.training.domain.vo.OutlineSimpleInfoVO;
import com.yuedongyun.training.domain.vo.OutlineVO;

import java.util.List;

/**
 * <p>
 * 目录草稿 服务类
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-19
 */
public interface ITrainingOutlineService extends IService<TrainingOutline> {

    /**
     * 查询线上训练目录
     *
     * @param trainingId 训练id
     * @return 训练目录
     */
    List<OutlineDTO> queryTrainingOutlines(Long trainingId, Boolean withPractice);

    /**
     * 批量统计媒资id引用次数
     *
     * @param mediaIds 媒资id
     * @return 媒资引用次数
     */
    List<MediaQuoteDTO> countMediaUserInfo(List<Long> mediaIds);

    /**
     * 获取简单的小节信息，
     *
     * @param sessionId 小节id
     * @return 训练id，媒资id，是否支持免费试看，免费试看时长
     */
    SessionInfoDTO getSimpleSessionInfo(Long sessionId);

    /**
     * 根据训练id获取训练的目录列表
     *
     * @param trainingId 训练id
     * @return 训练的目录列表
     */
    List<OutlineSimpleInfoVO> getOutlinesIndexList(Long trainingId);

    List<OutlineSimpleInfoVO> getManyOutlineSimpleInfo(List<Long> ids);

    OutlineSimpleInfoVO querySessionInfoById(Long id);

    List<OutlineVO> queryTrainingOutlinesVO(Long trainingId, Boolean withPractice);
}

