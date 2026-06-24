package com.yuedongyun.training.controller;

import com.yuedongyun.training.domain.dto.CategoryAddDTO;
import com.yuedongyun.training.domain.dto.CategoryDisableOrEnableDTO;
import com.yuedongyun.training.domain.dto.CategoryListDTO;
import com.yuedongyun.training.domain.dto.CategoryUpdateDTO;
import com.yuedongyun.training.domain.vo.CategoryInfoVO;
import com.yuedongyun.training.domain.vo.CategoryVO;
import com.yuedongyun.training.domain.vo.SimpleCategoryVO;
import com.yuedongyun.training.service.ICategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 训练分类接口
 */
@RestController
@Api(tags = "训练分类相关接口")
@RequestMapping("categorys")
@Slf4j
@Validated
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @GetMapping("list")
    @ApiOperation("查询训练分类信息")
    public List<CategoryVO> list(CategoryListDTO categoryListDTO) {
        log.info("list categoryListDTO : {}", categoryListDTO);
        return categoryService.list(categoryListDTO);
    }

    @GetMapping("{id}")
    @ApiOperation("获取训练分类信息")
    @ApiImplicitParams(@ApiImplicitParam(name = "id", value = "分类id"))
    public CategoryInfoVO get(@PathVariable("id") Long id) {
        return categoryService.get(id);
    }

    @PostMapping("add")
    @ApiOperation("新增训练分类")
    public void add(@Valid @RequestBody CategoryAddDTO categoryAddDTO) {
        categoryService.add(categoryAddDTO);
    }

    @DeleteMapping("{id}")
    @ApiOperation("删除分类信息")
    @ApiImplicitParams(@ApiImplicitParam(name = "id", value = "分类id"))
    public void delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
    }

    @PutMapping("disableOrEnable")
    @ApiOperation("训练分类停用或启用")
    public void disableOrEnable(@Validated @RequestBody CategoryDisableOrEnableDTO categoryDisableOrEnableDTO) {
        categoryService.disableOrEnable(categoryDisableOrEnableDTO);
    }

    @PutMapping("update")
    @ApiOperation("更新训练分类")
    public void updateCategory(@Validated @RequestBody CategoryUpdateDTO categoryUpdateDTO) {
        categoryService.update(categoryUpdateDTO);
    }

    @GetMapping("all")
    @ApiOperation("获取所有训练分类信息，仅包含id、名称及层级关系")
    public List<SimpleCategoryVO> all(@RequestParam(value = "admin", required = false, defaultValue = "0") Boolean admin) {
        return categoryService.all(admin);
    }

    @GetMapping("getAllOfOneLevel")
    @ApiOperation("获取所有训练分类，不区分层级")
    public List<CategoryVO> allOfOneLevel() {
        return categoryService.allOfOneLevel();
    }
}

