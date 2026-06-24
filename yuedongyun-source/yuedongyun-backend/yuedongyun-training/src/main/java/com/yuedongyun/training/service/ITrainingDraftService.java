package com.yuedongyun.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.api.dto.training.TrainingDTO;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.training.domain.dto.TrainingBaseInfoSaveDTO;
import com.yuedongyun.training.domain.dto.TrainingPageQuery;
import com.yuedongyun.training.domain.po.TrainingDraft;
import com.yuedongyun.training.domain.vo.TrainingBaseInfoVO;
import com.yuedongyun.training.domain.vo.TrainingPageVO;
import com.yuedongyun.training.domain.vo.TrainingSaveVO;
import com.yuedongyun.training.domain.vo.NameExistVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 训练草稿 服务类
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-18
 */
public interface ITrainingDraftService extends IService<TrainingDraft> {

    /**
     * 保存草稿
     *
     * @param trainingBaseInfoSaveDTO 训练基础信息
     */
    TrainingSaveVO save(TrainingBaseInfoSaveDTO trainingBaseInfoSaveDTO);

    /**
     * 如果用于编辑，需要先去草稿中拿出已经编辑的内容，如果不是用于编辑直接获取正式数据
     *
     * @param id 训练id
     * @param see 是否用于查看页面查看数据使用，不是的话就是编辑页面使用
     * @return 训练基本信息
     */
    TrainingBaseInfoVO getTrainingBaseInfo(Long id, Boolean see);

    /**
     * 修改训练草稿进行到哪一步了，步骤只能一步步升，不能跳填，不能往回填
     * @param id 训练id
     * @param step 完成的步数
     */
    void updateStep(Long id, Integer step);

    /**
     * 训练上架
     *
     * @param id 训练上架
     */
    void upShelf(Long id);

    void checkBeforeUpShelf(Long id);

    /**
     * 训练下架
     *
     * @param id 训练id
     */
    void downShelf(Long id);

    /**
     * 获取训练的搜索信息
     * @param id 训练id
     * @return 训练数据
     */
    TrainingDTO getTrainingDTOById(Long id);

    /**
     * 删除训练的草稿
     *
     * @param id 训练id
     */
    void delete(Long id);

    /**
     * 分页查询更新时间
     * @param trainingPageQuery 训练分页参数
     * @return 训练分页数据
     */
    PageDTO<TrainingPageVO> queryForPage(TrainingPageQuery trainingPageQuery);

    /**
     * 校验名称是否存在，或者被其他训练占用
     * @param name 训练名称
     * @param id 当前训练名称
     */
    NameExistVO checkName(String name, Long id);

    /**
     * 查询训练id列表中存在的id
     * @param idList
     * @return
     */
    List<Long> queryExists(List<Long> idList);

    /**
     * 统计草稿中的训练分类拥有的训练数量
     * @return
     */
    Map<Long, Integer> countTrainingNumOfCategory();
}

