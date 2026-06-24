package com.yuedongyun.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuedongyun.api.dto.IdAndNumDTO;
import com.yuedongyun.training.domain.po.TrainingDraft;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 训练草稿 Mapper 接口
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-21
 */
public interface TrainingDraftMapper extends BaseMapper<TrainingDraft> {
    String COLUMNS = "id, name, training_type, cover_url, first_cate_id, second_cate_id, third_cate_id, free, price, difficulty, train_part, calorie_burn, template_type, template_url, status, purchase_start_time, purchase_end_time, step, media_duration,valid_duration, session_num, dep_id, create_time, update_time, creater, updater,score,publish_time";

    @Insert("insert into training_draft(" + COLUMNS + ",can_update) (select " + COLUMNS + ",0 from training where id=#{id} )")
    int insertFromTraining(@Param("id") Long id);

    /**
     * 批量查询教练所负责的训练数量
     * @param coachIds
     * @return
     */
    @Select("<script>SELECT ct.coach_id as id,count(*) as num " +
            " from training_draft c LEFT JOIN training_coach_draft ct on c.id=ct.training_id " +
            "where c.status=1 and c.deleted=0 and ct.coach_id in (<foreach collection='coachIds' " +
            "item='coachId' separator=','>#{coachId}</foreach>)" +
            " GROUP BY ct.coach_id</script>")
    List<IdAndNumDTO> countTrainingNumOfCoach(@Param("coachIds")List<Long> coachIds);

    /**
     * 统计草稿中其他训练
     * @param name
     * @return
     */
    @Select("<script>select count(*) from training_draft where <if test='id!=null'> id !=#{id} and </if> name=#{name} and deleted=0 </script>")
    int countByNameAndId(@Param("name") String name, @Param("id") Long id);
}

