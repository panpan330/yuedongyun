package com.yuedongyun.user.controller;


import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.user.domain.query.UserPageQuery;
import com.yuedongyun.user.domain.vo.CoachPageVO;
import com.yuedongyun.user.service.ICoachService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 教练详情表 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2022-07-12
 */
@RestController
@RequestMapping("/coaches")
@Api(tags = "用户管理接口")
public class CoachController {

    @Autowired
    private ICoachService coachService;

    @GetMapping("/page")
    @ApiOperation("分页查询教练信息")
    public PageDTO<CoachPageVO> queryCoachPage(UserPageQuery pageQuery){
        return coachService.queryCoachPage(pageQuery);
    }
}
