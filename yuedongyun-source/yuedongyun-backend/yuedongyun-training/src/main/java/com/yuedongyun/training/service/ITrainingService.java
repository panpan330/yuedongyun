package com.yuedongyun.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.api.dto.training.*;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.training.domain.dto.TrainingPageQuery;
import com.yuedongyun.training.domain.dto.TrainingSimpleInfoListDTO;
import com.yuedongyun.training.domain.po.Training;
import com.yuedongyun.training.domain.vo.TrainingAndSessionVO;
import com.yuedongyun.training.domain.vo.TrainingPageVO;
import com.yuedongyun.training.domain.vo.NameExistVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 训练正式表 服务类
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
public interface ITrainingService extends IService<Training> {

    /**
     * 修改训练状态
     *
     * @param id 训练id
     * @param status 训练状态
     */
    void updateStatus(Long id, Integer status);

    TrainingDTO getTrainingDTOById(Long id);


    void delete(Long id);

    /**
     * 根据条件查询训练简单信息
     *
     * @param trainingSimpleInfoListDTO 训练三级分类列表
     * @return 训练信息列表
     */
    List<TrainingSimpleInfoDTO> getSimpleInfoList(TrainingSimpleInfoListDTO trainingSimpleInfoListDTO);

    /**
     * 查询教练出题数目和训练数目
     * @param coachIds 教练id
     * @return 教练的出题数量
     */
    List<SubNumAndTrainingNumDTO> countAssessmentNumAndTrainingNumOfCoach(List<Long> coachIds);

    /**
     * 训练完结
     */
    int trainingFinished();

    /**
     * 统计每个分类id所拥有的训练数量
     * @return 分类对应的训练数量
     */
    Map<Long, Integer> countTrainingNumOfCategory();

    /**
     * 查询有已上架训练分类的分类id
     * @return
     */
    List<Long> getCategoryIdListWithTraining();

    /**
     * 统计某个训练分类的训练数量
     *
     * @param categoryId 训练分类id
     * @return 训练数量
     */
    Integer countTrainingNumOfCategory(Long categoryId);

    /**
     * 查询训练的详细信息
     * @param id 训练id
     * @param withOutline 是否查询目录数据
     * @param withCoaches 是否查询教练数据
     * @return 训练详细信息
     */
    TrainingFullInfoDTO getInfoById(Long id, boolean withOutline, boolean withCoaches);

    /**
     * 分页查询训练信息
     * @param trainingPageQuery 分页参数
     * @return 训练分页数据
     */
    PageDTO<TrainingPageVO> queryForPage(TrainingPageQuery trainingPageQuery);
    /**
     * 根据训练分类id查询训练列表
     * @param categoryId 训练分类id
     * @param level 训练分类级别
     * @return
     */
    List<Training> queryByCategoryIdAndLevel(Long categoryId, Integer level);

    /**
     * 校验名称是否存在，或者被其他训练占用
     * @param name 训练名称
     * @param id 当前训练名称
     */
    NameExistVO checkName(String name, Long id);

    /**
     * 查询训练id列表中
     * @param idList
     * @return
     */
    List<Long> queryExists(List<Long> idList,List<Integer> statusList);

    /**
     * 根据训练name模糊查询训练id列表
     * @param name
     * @return
     */
    List<Long> queryTrainingIdByName(String name);

    TrainingAndSessionVO queryTrainingAndOutlinelogById(Long trainingId);
}

