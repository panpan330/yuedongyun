package com.yuedongyun.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuedongyun.api.client.trade.TradeClient;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.common.enums.UserType;
import com.yuedongyun.common.utils.BeanUtils;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.common.utils.RandomUtils;
import com.yuedongyun.user.constants.UserConstants;
import com.yuedongyun.user.domain.dto.MemberFormDTO;
import com.yuedongyun.user.domain.po.User;
import com.yuedongyun.user.domain.po.UserDetail;
import com.yuedongyun.user.domain.query.UserPageQuery;
import com.yuedongyun.user.domain.vo.MemberPageVo;
import com.yuedongyun.user.service.IMemberService;
import com.yuedongyun.user.service.IUserDetailService;
import com.yuedongyun.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 会员详情表 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2022-07-12
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements IMemberService {

    private final IUserService userService;
    private final IUserDetailService detailService;
    private final TradeClient tradeClient;

    @Override
    @Transactional
    public void saveMember(MemberFormDTO memberFormDTO) {
        // 1.新增用户账号
        User user = new User();
        user.setCellPhone(memberFormDTO.getCellPhone());
        user.setPassword(memberFormDTO.getPassword());
        user.setType(UserType.MEMBER);
        userService.addUserByPhone(user, memberFormDTO.getCode());

        // 2.新增会员详情
        UserDetail member = new UserDetail();
        member.setId(user.getId());
        member.setName(RandomUtils.randomString(8));
        member.setRoleId(UserConstants.MEMBER_ROLE_ID);
        detailService.save(member);
    }

    @Override
    public void updateMyPassword(MemberFormDTO memberFormDTO) {
        userService.updatePasswordByPhone(
                memberFormDTO.getCellPhone(), memberFormDTO.getCode(), memberFormDTO.getPassword()
        );
    }

    @Override
    public PageDTO<MemberPageVo> queryMemberPage(UserPageQuery query) {
        // 1.分页条件
        Page<UserDetail> page  =  detailService.queryUserDetailByPage(query, UserType.MEMBER);
        List<UserDetail> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }

        // 2.查询购买的训练数量
        List<Long> stuIds = records.stream().map(UserDetail::getId).collect(Collectors.toList());
        Map<Long, Integer> numMap = tradeClient.countEnrollTrainingOfMember(stuIds);

        // 3.处理vo
        List<MemberPageVo> list = new ArrayList<>(records.size());
        for (UserDetail r : records) {
            MemberPageVo v = BeanUtils.toBean(r, MemberPageVo.class);
            list.add(v);
            v.setTrainingAmount(numMap.get(r.getId()));
        }
        return new PageDTO<>(page.getTotal(), page.getPages(), list);
    }
}

