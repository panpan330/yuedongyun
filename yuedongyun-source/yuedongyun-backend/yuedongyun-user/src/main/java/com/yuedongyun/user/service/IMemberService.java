package com.yuedongyun.user.service;

import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.user.domain.dto.MemberFormDTO;
import com.yuedongyun.user.domain.query.UserPageQuery;
import com.yuedongyun.user.domain.vo.MemberPageVo;

/**
 * <p>
 * 会员详情表 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2022-07-12
 */
public interface IMemberService {

    void saveMember(MemberFormDTO memberFormDTO);

    void updateMyPassword(MemberFormDTO memberFormDTO);

    PageDTO<MemberPageVo> queryMemberPage(UserPageQuery pageQuery);
}
