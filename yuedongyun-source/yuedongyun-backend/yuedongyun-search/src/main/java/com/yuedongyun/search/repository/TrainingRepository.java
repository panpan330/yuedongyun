package com.yuedongyun.search.repository;

import com.yuedongyun.search.domain.po.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository{
    String INDEX_NAME = "training";
    String DEFAULT_QUERY_NAME = "name";
    String CATEGORY_ID_LV1 = "categoryIdLv1";
    String CATEGORY_ID_LV2 = "categoryIdLv2";
    String CATEGORY_ID_LV3 = "categoryIdLv3";
    String PUBLISH_TIME = "publishTime";
    String FREE = "free";
    String DIFFICULTY = "difficulty";
    String TRAIN_PART = "trainPart";
    String STATUS = "status";
    String TYPE = "type";
    String UPDATE_TIME = "updateTime";
    String SOLD = "sold";

    /**
     * <h1>更新sold的脚本</h1>
     * <pre>
     * {
     *   "script": {
     *     "lang": "painless",
     *     "source": "ctx._source.sold += params.count"
     *   }
     * }
     * </pre>
     */
    String INCREMENT_SOLD_SCRIPT_ID = "increment_sold";
    String INCREMENT_SOLD_SCRIPT_PARAM = "count";

    void save(Training training);

    void deleteById(Long trainingId);

    Optional<Training> findById(Long trainingId);

    void updateById(Long trainingId, Object ... docs);

    void increment(Long trainingId, String field, int amount);

    void incrementSold(List<Long> trainingIds, int amount);

    void saveAll(List<Training> list);

    void deleteByIds(List<Long> trainingIds);
}

