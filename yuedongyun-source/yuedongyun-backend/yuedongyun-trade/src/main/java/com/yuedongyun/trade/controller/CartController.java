package com.yuedongyun.trade.controller;


import com.yuedongyun.trade.domain.dto.CartsAddDTO;
import com.yuedongyun.trade.domain.vo.CartVO;
import com.yuedongyun.trade.service.ICartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 购物车条目信息，也就是购物车中的训练 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2022-08-28
 */
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Api(tags = "购物车相关接口")
public class CartController {

    private final ICartService cartService;

    @ApiOperation("添加训练到购物车")
    @PostMapping
    public void addTraining2Cart(@Valid @RequestBody CartsAddDTO cartsAddDTO){
        cartService.addTraining2Cart(cartsAddDTO.getTrainingId());
    }

    @ApiOperation("获取购物车中的训练")
    @GetMapping
    public List<CartVO> getMyCarts(){
        return cartService.getMyCarts();
    }

    @ApiOperation("删除指定的购物车条目")
    @DeleteMapping("/{id}")
    public void deleteCartById(@ApiParam("购物车条目id") @PathVariable("id") Long id){
        cartService.deleteCartById(id);
    }

    @ApiOperation("批量删除购物车条目")
    @DeleteMapping
    public void deleteCartById(@ApiParam("购物车条目id集合") @RequestParam("ids") List<Long> ids){
        cartService.deleteCartByIds(ids);
    }
}

