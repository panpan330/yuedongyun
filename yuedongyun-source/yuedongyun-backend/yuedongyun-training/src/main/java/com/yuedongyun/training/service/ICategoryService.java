package com.yuedongyun.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.training.domain.dto.CategoryAddDTO;
import com.yuedongyun.training.domain.dto.CategoryDisableOrEnableDTO;
import com.yuedongyun.training.domain.dto.CategoryListDTO;
import com.yuedongyun.training.domain.dto.CategoryUpdateDTO;
import com.yuedongyun.training.domain.po.Category;
import com.yuedongyun.training.domain.po.Training;
import com.yuedongyun.training.domain.vo.CategoryInfoVO;
import com.yuedongyun.training.domain.vo.CategoryVO;
import com.yuedongyun.training.domain.vo.SimpleCategoryVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 训练分类 服务类
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-14
 */
public interface ICategoryService extends IService<Category> {

    /**
     * 分页查询训练分类信息
     *
     * @param categoryPageDTO 分页参数
     * @return 训练分类分页信息
     */
    List<CategoryVO> list(CategoryListDTO categoryPageDTO);

    /**
     * 新增训练分类
     *
     * @param categoryAddDTO 分类信息
     */
    void add(CategoryAddDTO categoryAddDTO);

    /**
     * 获取训练分类信息
     * @param id 分类id
     * @return 训练分类信息
     */
    CategoryInfoVO get(Long id);

    /**
     * 删除训练分类
     * @param id 分类id
     */
    void delete(Long id);

    /**
     * 训练分类启用或禁用
     */
    void disableOrEnable(CategoryDisableOrEnableDTO categoryDisableOrEnableDTO);

    /**
     * 更新训练分类信息
     */
    void update(CategoryUpdateDTO categoryUpdateDTO);

    /**
     * 获取所有分类的数据及结构
     */
    List<SimpleCategoryVO> all(Boolean admin);

    /**
     * 获取训练分类id和名称
     * @return 训练分类id和名称
     */
    Map<Long, String> getCateIdAndName();

    List<CategoryVO> allOfOneLevel();

    /**
     * 根据训练分类id查询分类列表
     * @param ids  训练分类id
     * @return 分类列表
     */
    List<Category> queryByIds(List<Long> ids);

    /**
     * 根据三级训练分类查询训练分类信息
     * @param thirdCateIdList 三级训练分类
     * @return 训练分类信息
     */
    Map<Long, String> queryByThirdCateIds(@RequestParam("thirdCateIdList") List<Long> thirdCateIdList);

    /**
     * 获取训练分类信息
     *
     * @param training
     * @return
     */
    List<String> queryTrainingCategorys(Training training);

    /**
     * 校验训练分类是否符合要求,并按顺序返回一二三级训练分类id列表
     *
     * @param thirdCateId 三级训练分类
     * @return 一二三级训练分类id列表
     */
    List<Long> checkCategory(Long thirdCateId);
}

