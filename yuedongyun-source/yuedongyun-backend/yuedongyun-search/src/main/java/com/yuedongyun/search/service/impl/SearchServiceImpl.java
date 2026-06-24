package com.yuedongyun.search.service.impl;

import com.yuedongyun.api.cache.CategoryCache;
import com.yuedongyun.api.client.user.UserClient;
import com.yuedongyun.api.dto.user.UserDTO;
import com.yuedongyun.common.constants.ErrorInfo;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.common.exceptions.CommonException;
import com.yuedongyun.common.utils.*;
import com.yuedongyun.search.config.InterestsProperties;
import com.yuedongyun.search.constants.SearchErrorInfo;
import com.yuedongyun.search.domain.po.Training;
import com.yuedongyun.search.domain.query.TrainingPageQuery;
import com.yuedongyun.search.domain.vo.TrainingVO;
import com.yuedongyun.search.repository.TrainingRepository;
import com.yuedongyun.search.service.IInterestsService;
import com.yuedongyun.search.service.ISearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.yuedongyun.search.repository.TrainingRepository.PUBLISH_TIME;

@Service
public class SearchServiceImpl implements ISearchService {

    @Autowired
    private RestHighLevelClient restClient;

    @Autowired
    private IInterestsService interestsService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private CategoryCache categoryCache;

    @Autowired
    private InterestsProperties interestsProperties;

    @Override
    public List<TrainingVO> queryTrainingByCateId(Long cateLv2Id) {
        return queryTopNByCategoryIdLv2sAndFree(
                CollUtils.singletonList(cateLv2Id), null, PUBLISH_TIME, false, 10);
    }

    @Override
    public List<TrainingVO> queryBestTopN() {
        // 1.获取当前用户
        return queryTopNTrainingOnMarketByFree(false, TrainingRepository.SOLD);
    }

    @Override
    public List<TrainingVO> queryNewTopN() {
        return queryTopNTrainingOnMarketByFree(false, PUBLISH_TIME);
    }

    @Override
    public List<TrainingVO> queryFreeTopN() {
        return queryTopNTrainingOnMarketByFree(true, TrainingRepository.SOLD);
    }

    private List<TrainingVO> queryTopNTrainingOnMarketByFree(boolean isFree, String sortBy) {
        // 1.获取当前用户
        Long id = UserContext.getUser();
        // 2.查询训练
        List<TrainingVO> trainings = null;
        if (id == null) {
            // 3.未登录，直接查询报名人数最多的
            trainings = queryTopNByCategoryIdLv2sAndFree(
                    null, isFree, sortBy, false, interestsProperties.getTopNumber());
        } else {
            // 4.已登录，根据兴趣爱好查询
            List<Long> categoryIds = interestsService.queryMyInterestsIds();
            if (CollUtils.isEmpty(categoryIds)) {
                // 4.1.没有兴趣爱好，直接查询报名人数最多的
                trainings = queryTopNByCategoryIdLv2sAndFree(
                        null, isFree, sortBy, false, interestsProperties.getTopNumber());
            } else {
                // 4.2.有爱好.查询爱好训练中报名人数最多的
                trainings = queryTopNByCategoryIdLv2sAndFree(
                        categoryIds, isFree, sortBy, false, interestsProperties.getTopNumber());
            }
        }
        return trainings;
    }

    private List<TrainingVO> queryTopNByCategoryIdLv2sAndFree(
            List<Long> categoryIds, Boolean isFree, String sortBy, boolean isASC, int n) {
        // 1.准备Request
        SearchRequest request = new SearchRequest(TrainingRepository.INDEX_NAME);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 1.1.是否免费
        if(isFree != null) {
            queryBuilder.filter(QueryBuilders.termQuery(TrainingRepository.FREE, isFree));
        }
        // 1.2.分类id
        if (categoryIds != null) {
            if (categoryIds.size() == 1) {
                queryBuilder.filter(QueryBuilders.termQuery(TrainingRepository.CATEGORY_ID_LV2, categoryIds.get(0)));
            } else {
                queryBuilder.filter(QueryBuilders.termsQuery(TrainingRepository.CATEGORY_ID_LV2, categoryIds));
            }
        }
        if(isFree != null || categoryIds != null) {
            request.source().query(queryBuilder);
        }
        // 1.3.TopN
        request.source().size(n).sort(sortBy, isASC ? SortOrder.ASC : SortOrder.DESC);
        // 2.发送请求
        SearchResponse response = null;
        try {
            response = restClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new CommonException(SearchErrorInfo.QUERY_TRAINING_ERROR, e);
        }
        // 3.解析
        SearchHits searchHits = response.getHits();
        SearchHit[] hits = searchHits.getHits();
        if (hits == null || hits.length == 0) {
            return CollUtils.emptyList();
        }
        List<TrainingVO> trainings = new ArrayList<>(hits.length);
        Set<Long> coachIds = new HashSet<>(hits.length);
        for (SearchHit hit : hits) {
            // 3.1.数据转换
            TrainingVO vo = JsonUtils.toBean(hit.getSourceAsString(), TrainingVO.class);
            // 3.2.获取分类id
            coachIds.add(Long.valueOf(vo.getCoach()));
            // 3.3.保存
            trainings.add(vo);
        }
        coachIds.remove(0L);
        if (coachIds.size() == 0) {
            return trainings;
        }
        // 4.查询教练
        List<UserDTO> coaches = userClient.queryUserByIds(coachIds);
        AssertUtils.isNotEmpty(coaches, SearchErrorInfo.COACH_NOT_EXISTS);
        Map<String, String> tMap = coaches.stream()
                .collect(Collectors.toMap(t -> t.getId().toString(), UserDTO::getName));
        for (TrainingVO c : trainings) {
            c.setCoach(tMap.getOrDefault(c.getCoach(), "匿名"));
        }
        return trainings;
    }

    @Override
    public PageDTO<TrainingVO> queryTrainingsForPortal(TrainingPageQuery query) {
        // 1.搜索数据
        SearchResponse response = searchForResponse(query, TrainingVO.EXCLUDE_FIELDS);
        // 2.解析响应
        PageDTO<Training> result = handleSearchResponse(response, query.getPageSize());
        // 3.处理VO
        List<Training> list = result.getList();
        if (CollUtils.isEmpty(list)) {
            return PageDTO.empty(result.getTotal(), result.getPages());
        }
        // 3.1.查询教练信息
        List<Long> coachIds = list.stream().map(Training::getCoach).collect(Collectors.toList());
        List<UserDTO> coaches = userClient.queryUserByIds(coachIds);
        AssertUtils.isNotEmpty(coaches, SearchErrorInfo.COACH_NOT_EXISTS);
        Map<Long, String> coachMap = coaches.stream()
                .collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));
        // 3.2.转换VO
        List<TrainingVO> vos = new ArrayList<>(list.size());
        for (Training c : list) {
            TrainingVO vo = BeanUtils.toBean(c, TrainingVO.class);
            vo.setCoach(coachMap.getOrDefault(c.getCoach(), "未知"));
            vos.add(vo);
        }
        return new PageDTO<>(result.getTotal(), result.getPages(), vos);
    }

    @Override
    public List<Long> queryTrainingsIdByName(String keyword) {
        // 1.创建Request
        SearchRequest request = new SearchRequest(TrainingRepository.INDEX_NAME);
        // 2.构建DSL
        request.source()
                .query(QueryBuilders.matchPhraseQuery(TrainingRepository.DEFAULT_QUERY_NAME, keyword))
                .fetchSource(new String[]{"id"}, null);
        // 3.查询
        SearchResponse response;
        try {
            response = restClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new CommonException(SearchErrorInfo.QUERY_TRAINING_ERROR, e);
        }
        // 4.解析
        SearchHits searchHits = response.getHits();
        // 4.1.获取hits
        SearchHit[] hits = searchHits.getHits();
        if (hits.length == 0) {
            return CollUtils.emptyList();
        }
        // 4.2.获取id
        return Arrays.stream(hits)
                .map(SearchHit::getId)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }


    private SearchResponse searchForResponse(TrainingPageQuery query, String[] excludeFields) {
        // 1.创建Request
        SearchRequest request = new SearchRequest(TrainingRepository.INDEX_NAME);
        // 2.构建DSL
        // 2.1.构建query
        buildBasicQuery(request, query);
        // 2.2.排序
        String sortBy = query.getSortBy();
        if (StringUtils.isNotBlank(sortBy)) {
            request.source().sort(sortBy, query.getIsAsc() ? SortOrder.ASC : SortOrder.DESC);
        }
        // 2.3.分页
        request.source().from(query.from()).size(query.getPageSize());
        // 2.4.高亮
        request.source().highlighter(new HighlightBuilder().field(TrainingRepository.DEFAULT_QUERY_NAME));
        // 2.5.source处理
        request.source().fetchSource(null, excludeFields);
        // 3.发送请求
        SearchResponse response = null;
        try {
            response = restClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new CommonException(ErrorInfo.Msg.SERVER_INTER_ERROR, e);
        }
        return response;
    }

    private void buildBasicQuery(SearchRequest request, TrainingPageQuery query) {
        // 1.准备bool查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 2.关键字搜索
        String keyword = query.getKeyword();
        if (StringUtils.isBlank(keyword)) {
            queryBuilder.must(QueryBuilders.matchAllQuery());
        } else {
            queryBuilder.must(QueryBuilders.matchPhraseQuery(TrainingRepository.DEFAULT_QUERY_NAME, keyword));
        }
        // 3.其它条件
        if (query.getCategoryIdLv1() != null) {
            queryBuilder.filter(QueryBuilders.termQuery(TrainingRepository.CATEGORY_ID_LV1, query.getCategoryIdLv1()));
        }
        if (query.getCategoryIdLv2() != null) {
            queryBuilder.filter(QueryBuilders.termQuery(TrainingRepository.CATEGORY_ID_LV2, query.getCategoryIdLv2()));
        }
        if (query.getCategoryIdLv3() != null) {
            queryBuilder.filter(QueryBuilders.termQuery(TrainingRepository.CATEGORY_ID_LV3, query.getCategoryIdLv3()));
        }
        if (query.getFree() != null) {
            queryBuilder.filter(QueryBuilders.termQuery(TrainingRepository.FREE, query.getFree()));
        }
        if (query.getDifficulty() != null) {
            queryBuilder.filter(QueryBuilders.termQuery(TrainingRepository.DIFFICULTY, query.getDifficulty()));
        }
        if (StringUtils.isNotBlank(query.getTrainPart())) {
            queryBuilder.filter(QueryBuilders.termQuery(TrainingRepository.TRAIN_PART, query.getTrainPart()));
        }
        if (query.getType() != null) {
            queryBuilder.filter(QueryBuilders.termQuery(TrainingRepository.TYPE, query.getType()));
        }
        LocalDateTime beginTime = query.getBeginTime();
        LocalDateTime endTime = query.getEndTime();
        if(beginTime != null || endTime != null) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(TrainingRepository.UPDATE_TIME);
            if (beginTime != null) {
                rangeQuery.gte(beginTime);
            }
            if (endTime != null) {
                rangeQuery.lte(endTime);
            }
            queryBuilder.filter(rangeQuery);
        }
        // 4.写入request
        request.source().query(queryBuilder);
    }

    private PageDTO<Training> handleSearchResponse(SearchResponse response, int pageSize) {
        SearchHits searchHits = response.getHits();
        // 1.总条数
        long total = searchHits.getTotalHits().value;
        // 2.总页数
        long totalPages = (total + pageSize - 1) / pageSize;
        // 3.获取命中的数据
        SearchHit[] hits = searchHits.getHits();
        if (hits.length <= 0) {
            return new PageDTO<>(total, totalPages, CollUtils.emptyList());
        }
        // 4.遍历
        List<Training> list = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            // 5.获取某一条source
            String jsonSource = hit.getSourceAsString();
            // 6.反序列化
            Training training = JsonUtils.toBean(jsonSource, Training.class);
            // 7.处理高亮
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (CollUtils.isNotEmpty(highlightFields)) {
                // 7.1.获取高亮结果
                HighlightField field = highlightFields.get(TrainingRepository.DEFAULT_QUERY_NAME);
                Object[] fragments = field.getFragments();
                String value = StringUtils.join(fragments);
                // 7.2.覆盖非高亮结果
                training.setName(value);
            }
            list.add(training);
        }
        return new PageDTO<>(total, totalPages, list);
    }
}

