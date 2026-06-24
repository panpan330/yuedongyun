package com.yuedongyun.workout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.workout.domain.dto.QuestionFormDTO;
import com.yuedongyun.workout.domain.po.InteractionQuestion;
import com.yuedongyun.workout.domain.query.QuestionAdminPageQuery;
import com.yuedongyun.workout.domain.query.QuestionPageQuery;
import com.yuedongyun.workout.domain.vo.QuestionAdminVO;
import com.yuedongyun.workout.domain.vo.QuestionVO;

/**
 * <p>
 * 互动提问的问题表 服务类
 * </p>
 *
 * @author 虎哥
 */
public interface IInteractionQuestionService extends IService<InteractionQuestion> {

    void saveQuestion(QuestionFormDTO questionDTO);

    PageDTO<QuestionVO> queryQuestionPage(QuestionPageQuery query);

    QuestionVO queryQuestionById(Long id);

    PageDTO<QuestionAdminVO> queryQuestionPageAdmin(QuestionAdminPageQuery query);

    QuestionAdminVO queryQuestionByIdAdmin(Long id);

    void hiddenQuestion(Long id, Boolean hidden);

    void updateQuestion(Long id, QuestionFormDTO questionDTO);

    void deleteById(Long id);
}

