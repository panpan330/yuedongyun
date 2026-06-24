package com.yuedongyun.api.client.training;

import com.yuedongyun.api.dto.training.OutlineSimpleInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "outline", value = "training-service",path = "outlines")
public interface OutlineClient {

    /**
     * 根据目录id列表查询目录信息
     *
     * @param ids 目录id列表
     * @return id列表中对应的目录基础信息
     */
    @GetMapping("/batchQuery")
    List<OutlineSimpleInfoDTO> batchQueryOutline(@RequestParam("ids") Iterable<Long> ids);


}
