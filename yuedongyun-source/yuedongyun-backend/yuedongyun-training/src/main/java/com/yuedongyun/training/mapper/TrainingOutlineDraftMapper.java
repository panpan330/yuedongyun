package com.yuedongyun.training.mapper;

import com.yuedongyun.training.domain.po.TrainingOutlineDraft;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 目录草稿 Mapper 接口
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-19
 */
public interface TrainingOutlineDraftMapper extends BaseMapper<TrainingOutlineDraft> {

    String COLUMNS = "id, name, trailer, training_id, type, parent_outline_id, media_id, video_id, video_name, living_start_time, living_end_time, play_back, c_index, media_duration, dep_id, create_time, update_time, creater, updater";
    /**
     * 查询出需要更新到架上的目录数据
     */
    @Select("select id, name, trailer, training_id, type, parent_outline_id, media_id,  video_name, c_index, " +
            "media_duration, dep_id from training_outline_draft where training_id=#{trainingId}")
    @ResultMap("BaseResultMap")
    List<TrainingOutlineDraft> getByTrainingId(@Param("trainingId") Long trainingId);

    @Delete("<script>delete from training_outline_draft where training_id=#{trainingId}" +
            " and type in (<foreach collection='types' item='type' separator=','>#{type}</foreach>)</script>")
    int deleteByTrainingId(@Param("trainingId") Long couseId, @Param("types")List<Integer> types);


    @Insert("insert into training_outline_draft(" + COLUMNS + ",can_update) " +
            "(select " + COLUMNS + ",0 from training_outline where training_id=#{trainingId})" )
    int insertFromTrainingOutline(@Param("trainingId") Long trainingId);

    @Select("SELECT id FROM training_outline_draft WHERE training_id=#{trainingId} AND type IN (2, 3)")
    List<Long> getSessionIdByTrainingId(Long trainingId);
}

