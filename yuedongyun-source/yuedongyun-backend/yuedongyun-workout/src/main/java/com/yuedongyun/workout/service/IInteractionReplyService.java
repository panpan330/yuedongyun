package com.yuedongyun.workout.service;

import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.workout.domain.dto.ReplyDTO;
import com.yuedongyun.workout.domain.po.InteractionReply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.workout.domain.query.ReplyPageQuery;
import com.yuedongyun.workout.domain.vo.ReplyVO;

/**
 * <p>
 * 互动问题的回答或评论 服务类
 * </p>
 *
 * @author 虎哥
 */
public interface IInteractionReplyService extends IService<InteractionReply> {

    void saveReply(ReplyDTO replyDTO);

    PageDTO<ReplyVO> queryReplyPage(ReplyPageQuery pageQuery, boolean isMember);

    void hiddenReply(Long id, Boolean hidden);

    ReplyVO queryReplyById(Long id);
}

