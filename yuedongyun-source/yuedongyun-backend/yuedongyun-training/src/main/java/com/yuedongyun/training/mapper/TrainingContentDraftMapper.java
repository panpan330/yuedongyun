package com.yuedongyun.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuedongyun.training.domain.po.TrainingContentDraft;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 训练内容，主要是一些大文本 Mapper 接口
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-18
 */
public interface TrainingContentDraftMapper extends BaseMapper<TrainingContentDraft> {

    String COLUMNS ="id,training_introduce,use_people,training_detail,dep_id,create_time,update_time,creater,updater,deleted";

    @Insert("insert into training_content_draft (" + COLUMNS +
            ") (select " + COLUMNS + " from training_content where id=#{id})")
    int insertFromTrainingContent(@Param("id") Long id);

}

