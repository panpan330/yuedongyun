package com.yuedongyun.campaign.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 优惠券作用范围信息
 * </p>
 *
 * @author 虎哥
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("voucher_scope")
public class VoucherScope implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 范围限定类型：1-分类，2-训练，等等
     */
    private Integer type;

    /**
     * 优惠券id
     */
    private Long voucherId;

    /**
     * 优惠券作用范围的业务id，例如分类id、训练id
     */
    private Long bizId;


}

