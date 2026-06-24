package com.yuedongyun.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuedongyun.api.dto.IdAndNumDTO;
import com.yuedongyun.training.domain.po.Category3PO;
import com.yuedongyun.training.domain.po.Training;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 训练正式表 Mapper 接口
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-22
 */
public interface TrainingMapper extends BaseMapper<Training> {
    @Select("select count(1) from training where name = #{name}")
    int countSameName(@Param("name") String name);

    int updateVariableById(@Param("po") Training training);

    /**
     * 批量查询教练所负责的训练数量
     * @param coachIds
     * @return
     */
    @Select("<script>SELECT ct.coach_id as id,count(*) as num " +
            " from training c LEFT JOIN training_coach ct on c.id=ct.training_id " +
            "where c.status!=1 and c.deleted=0 and ct.coach_id in (<foreach collection='coachIds' " +
            "item='coachId' separator=','>#{coachId}</foreach>)" +
            " GROUP BY ct.coach_id</script>")
    List<IdAndNumDTO> countTrainingNumOfCoach(@Param("coachIds")List<Long> coachIds);

    @Select("select distinct first_cate_id as 'firstCateId',second_cate_id as 'secondCateId'," +
            "third_cate_id as 'thirdCateId' from training where status=2")
    List<Category3PO> queryCategoryIdWithTraining();
}

