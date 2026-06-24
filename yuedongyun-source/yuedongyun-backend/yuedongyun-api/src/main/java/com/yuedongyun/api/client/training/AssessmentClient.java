package com.yuedongyun.api.client.training;

import com.yuedongyun.api.dto.training.AssessmentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "training-service", path = "assessments")
public interface AssessmentClient {

    @GetMapping("list")
    List<AssessmentDTO> queryByIds(@RequestParam("ids") Iterable<Long> ids);
}

