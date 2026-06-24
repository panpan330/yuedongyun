package com.yuedongyun.training.mapper;

import com.yuedongyun.training.domain.po.AssessmentCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 训练分类关系表 Mapper 接口
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-15
 */
public interface AssessmentCategoryMapper extends BaseMapper<AssessmentCategory> {

    @Insert("<script>INSERT INTO assessment_category (assessment_id,first_cate_id,second_cate_id,third_cate_id) " +
            "value <foreach collection='scs' item='sc'  separator=','>(#{sc.assessmentId},#{sc.firstCateId}," +
            "#{sc.secondCateId},#{sc.thirdCateId})</foreach></script>")
    int batchInsert(@Param("scs") List<AssessmentCategory> assessmentCategoryList);

    /**
     * 获取指定训练分类下所有题目的数量
     *
     * @param categoryId
     * @param level
     * @return
     */
    @Select("<script>select count(*) from assessment_category where 1= 1 <if test='level==1'> and first_cate_id=#{categoryId} </if> " +
            "<if test='level==2'> and second_cate_id=#{categoryId} </if>" +
            "<if test='level==3'> and third_cate_id=#{categoryId} </if>" +
            "</script>")
    int countAssessmentNum(@Param("categoryId") Long categoryId, @Param("level") Integer level);

}

