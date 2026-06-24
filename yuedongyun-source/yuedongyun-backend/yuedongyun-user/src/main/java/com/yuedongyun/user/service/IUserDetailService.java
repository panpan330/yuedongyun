package com.yuedongyun.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.user.domain.po.UserDetail;
import com.yuedongyun.user.domain.query.UserPageQuery;
import com.yuedongyun.common.enums.UserType;

import java.util.List;

/**
 * <p>
 * 教练详情表 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2022-08-15
 */
public interface IUserDetailService extends IService<UserDetail> {

    UserDetail queryById(Long userId);

    List<UserDetail> queryByIds(List<Long> ids);

    Page<UserDetail> queryUserDetailByPage(UserPageQuery pageQuery, UserType type);
}
