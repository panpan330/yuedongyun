package com.yuedongyun.api.client.training;

import com.yuedongyun.api.dto.training.CategoryBasicDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(contextId = "category",value = "training-service",path = "categorys")
public interface CategoryClient {

    /**
     * 获取所有训练及训练分类
     * @return  所有训练及训练分类
     */
    @GetMapping("getAllOfOneLevel")
    List<CategoryBasicDTO> getAllOfOneLevel();
}

