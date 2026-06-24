package com.yuedongyun.user.service;

import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.user.domain.query.UserPageQuery;
import com.yuedongyun.user.domain.vo.CoachPageVO;

/**
 * <p>
 * 教练详情表 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2022-07-12
 */
public interface ICoachService{
    PageDTO<CoachPageVO> queryCoachPage(UserPageQuery pageQuery);

}
