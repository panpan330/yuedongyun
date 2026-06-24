package com.yuedongyun.campaign.controller;

import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.campaign.domain.dto.VoucherFormDTO;
import com.yuedongyun.campaign.domain.dto.VoucherIssueFormDTO;
import com.yuedongyun.campaign.domain.query.VoucherQuery;
import com.yuedongyun.campaign.domain.vo.VoucherDetailVO;
import com.yuedongyun.campaign.domain.vo.VoucherPageVO;
import com.yuedongyun.campaign.domain.vo.VoucherVO;
import com.yuedongyun.campaign.service.IVoucherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 优惠券的规则信息 控制器
 * </p>
 *
 * @author 虎哥
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/vouchers")
@Api(tags = "优惠券相关接口")
public class VoucherController {

    private final IVoucherService voucherService;

    @ApiOperation("新增优惠券接口")
    @PostMapping
    public void saveVoucher(@RequestBody @Valid VoucherFormDTO dto){
        voucherService.saveVoucher(dto);
    }

    @ApiOperation("分页查询优惠券接口")
    @GetMapping("/page")
    public PageDTO<VoucherPageVO> queryVoucherByPage(VoucherQuery query){
        return voucherService.queryVoucherByPage(query);
    }

    @ApiOperation("根据id查询优惠券接口")
    @GetMapping("/{id}")
    public VoucherDetailVO queryVoucherById(@ApiParam("优惠券id") @PathVariable("id") Long id){
        return voucherService.queryVoucherById(id);
    }

    @ApiOperation("发放优惠券接口")
    @PutMapping("/{id}/issue")
    public void beginIssue(@RequestBody @Valid VoucherIssueFormDTO dto) {
        voucherService.beginIssue(dto);
    }

    @ApiOperation("暂停发放优惠券接口")
    @PutMapping("/{id}/pause")
    public void pauseIssue(@ApiParam("优惠券id") @PathVariable("id") Long id) {
        voucherService.pauseIssue(id);
    }

    @ApiOperation("查询发放中的优惠券列表")
    @GetMapping("/list")
    public List<VoucherVO> queryIssuingVouchers(){
        return voucherService.queryIssuingVouchers();
    }

    @ApiOperation("删除优惠券")
    @DeleteMapping("{id}")
    public void deleteById(@ApiParam("优惠券id") @PathVariable("id") Long id) {
        voucherService.deleteById(id);
    }
}

