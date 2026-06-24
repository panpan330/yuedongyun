package com.yuedongyun.user.controller;


import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.user.domain.dto.MemberFormDTO;
import com.yuedongyun.user.domain.query.UserPageQuery;
import com.yuedongyun.user.domain.vo.MemberPageVo;
import com.yuedongyun.user.service.IMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 会员详情表 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2022-07-12
 */
@RestController
@RequestMapping("/members")
@Api(tags = "用户管理接口")
public class MemberController {

    @Autowired
    private IMemberService memberService;

    @ApiOperation("分页查询会员信息")
    @GetMapping("/page")
    public PageDTO<MemberPageVo> queryMemberPage(UserPageQuery pageQuery){
        return memberService.queryMemberPage(pageQuery);
    }

    @ApiOperation("会员注册")
    @PostMapping("/register")
    public void registerMember(@RequestBody MemberFormDTO memberFormDTO) {
        memberService.saveMember(memberFormDTO);
    }

    @ApiOperation("修改会员密码")
    @PutMapping("/password")
    public void updateMyPassword(@RequestBody MemberFormDTO memberFormDTO) {
        memberService.updateMyPassword(memberFormDTO);
    }
}
