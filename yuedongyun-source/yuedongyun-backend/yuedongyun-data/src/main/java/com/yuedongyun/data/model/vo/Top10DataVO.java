package com.yuedongyun.data.model.vo;

import com.yuedongyun.data.model.po.TrainingInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName Top10DataVO
 * @Author wusongsong
 * @Date 2022/10/10 19:33
 * @Version
 **/
@Data
public class Top10DataVO {
    // 热门训练
    private List<TrainingInfo> hot;
    // 热销训练
    private List<TrainingInfo> hotSales;
}

