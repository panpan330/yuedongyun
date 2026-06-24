package com.yuedongyun.search.domain.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Training {
    @Id
    private Long id;
    /** 训练名称 */
    private String name;
    /** 分类id */
    private Long categoryIdLv1;
    /** 分类id2 */
    private Long categoryIdLv2;
    /** 分类id3 */
    private Long categoryIdLv3;
    /** 是否免费 */
    private Boolean free;
    /** 训练类型：1：直播课，2：录播课 */
    private Integer type;
    /** 训练销量，报名人数 */
    private Integer sold;
    /** 价格 */
    private Integer price;
    /** 训练难度，1：入门，2：进阶，3：高阶 */
    private Integer difficulty;
    /** 训练重点部位 */
    private String trainPart;
    /** 预计热量消耗 */
    private Integer calorieBurn;
    /** 训练评分 */
    private Integer score;
    /** 教练id */
    private Long coach;
    /** 章节数量 */
    private Integer sessions;
    /** 训练封面 */
    private String coverUrl;
    private LocalDateTime publishTime;

    @JsonIgnore
    public List<Long> getCategoryIds(){
        return List.of(categoryIdLv1, categoryIdLv2, categoryIdLv3);
    }
}

