package com.yuedongyun.api.client.training;

import com.yuedongyun.api.dto.training.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "training", value = "training-service")
public interface TrainingClient {

    /**
     * 根据教练id列表获取教练出题数据和带练数据
     * @param coachIds 教练id列表
     * @return 教练id和教练对应的出题数和带练数
     */
    @GetMapping("/training/infoByCoachIds")
    List<SubNumAndTrainingNumDTO> infoByCoachIds(@RequestParam("coachIds") Iterable<Long> coachIds);

    /**
     * 根据小节id获取小节对应的mediaId和训练id
     *
     * @param sessionId 小节id
     * @return 小节对应的mediaId和训练id
     */
    @GetMapping("/training/session/{id}")
    SessionInfoDTO sessionInfo(@PathVariable("id") Long sessionId);

    /**
     * 根据媒资Id列表查询媒资被引用的次数
     *
     * @param mediaIds 媒资id列表
     * @return 媒资id和媒资被引用的次数的列表
     */
    @GetMapping("/training/media/useInfo")
    List<MediaQuoteDTO> mediaUserInfo(@RequestParam("mediaIds") Iterable<Long> mediaIds);

    /**
     * 根据训练id查询索引库需要的数据
     *
     * @param id 训练id
     * @return 索引库需要的数据
     */
    @GetMapping("/training/{id}/searchInfo")
    TrainingSearchDTO getSearchInfo(@PathVariable("id") Long id);

    /**
     * 根据训练id集合查询训练简单信息
     * @param ids id集合
     * @return 训练简单信息的列表
     */
    @GetMapping("/trainings/simpleInfo/list")
    List<TrainingSimpleInfoDTO> getSimpleInfoList(@RequestParam("ids") Iterable<Long> ids);

    /**
     * 根据训练id，获取训练、目录、教练信息
     * @param id 训练id
     * @return 训练信息、目录信息、教练信息
     */
    @GetMapping("/training/{id}")
    TrainingFullInfoDTO getTrainingInfoById(
            @PathVariable("id") Long id,
            @RequestParam(value = "withOutline", required = false) boolean withOutline,
            @RequestParam(value = "withCoaches", required = false) boolean withCoaches
    );
}

