package com.yuedongyun.workout.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.api.client.user.UserClient;
import com.yuedongyun.api.dto.user.UserDTO;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.common.utils.DateUtils;
import com.yuedongyun.common.utils.UserContext;
import com.yuedongyun.workout.constants.RedisConstants;
import com.yuedongyun.workout.domain.po.FitPointBoard;
import com.yuedongyun.workout.domain.query.FitPointBoardQuery;
import com.yuedongyun.workout.domain.vo.FitPointBoardItemVO;
import com.yuedongyun.workout.domain.vo.FitPointBoardVO;
import com.yuedongyun.workout.mapper.FitPointBoardMapper;
import com.yuedongyun.workout.service.IFitPointBoardService;
import com.yuedongyun.workout.utils.TableInfoContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.yuedongyun.workout.constants.WorkoutConstants.FITPOINTS_BOARD_TABLE_PREFIX;

/**
 * <p>
 * 学霸天梯榜 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Service
@RequiredArgsConstructor
public class FitPointBoardServiceImpl extends ServiceImpl<FitPointBoardMapper, FitPointBoard> implements IFitPointBoardService {

    private final StringRedisTemplate redisTemplate;

    private final UserClient userClient;

    @Override
    public FitPointBoardVO queryFitPointBoardBySeason(FitPointBoardQuery query) {
        // 1.判断是否是查询当前赛季
        Long season = query.getSeason();
        boolean isCurrent = season == null || season == 0;
        // 2.获取Redis的Key
        LocalDateTime now = LocalDateTime.now();
        String key = RedisConstants.FITPOINTS_BOARD_KEY_PREFIX + now.format(DateUtils.FITPOINTS_BOARD_SUFFIX_FORMATTER);
        // 2.查询我的积分和排名
        FitPointBoard myBoard = isCurrent ?
                queryMyCurrentBoard(key) : // 查询当前榜单（Redis）
                queryMyHistoryBoard(season); // 查询历史榜单（MySQL）
        // 3.查询榜单列表
        List<FitPointBoard> list = isCurrent ?
                queryCurrentBoardList(key, query.getPageNo(), query.getPageSize()) :
                queryHistoryBoardList(query);
        // 4.封装VO
        FitPointBoardVO vo = new FitPointBoardVO();
        // 4.1.处理我的信息
        if (myBoard != null) {
            vo.setFitPoint(myBoard.getFitPoint());
            vo.setRank(myBoard.getRank());
        }
        if (CollUtils.isEmpty(list)) {
            return vo;
        }
        // 4.2.查询用户信息
        Set<Long> uIds = list.stream().map(FitPointBoard::getUserId).collect(Collectors.toSet());
        List<UserDTO> users = userClient.queryUserByIds(uIds);
        Map<Long, String> userMap = new HashMap<>(uIds.size());
        if(CollUtils.isNotEmpty(users)) {
            userMap = users.stream().collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));
        }
        // 4.3.转换VO
        List<FitPointBoardItemVO> items = new ArrayList<>(list.size());
        for (FitPointBoard p : list) {
            FitPointBoardItemVO v = new FitPointBoardItemVO();
            v.setFitPoint(p.getFitPoint());
            v.setRank(p.getRank());
            v.setName(userMap.get(p.getUserId()));
            items.add(v);
        }
        vo.setBoardList(items);
        return vo;
    }

    @Override
    public void createFitPointBoardTableBySeason(Integer season) {
        getBaseMapper().createFitPointBoardTable(FITPOINTS_BOARD_TABLE_PREFIX + season);
    }

    private List<FitPointBoard> queryHistoryBoardList(FitPointBoardQuery query) {
        // 1.计算表名
        TableInfoContext.setInfo(FITPOINTS_BOARD_TABLE_PREFIX + query.getSeason());
        // 2.查询数据
        Page<FitPointBoard> page = page(query.toMpPage());
        // 3.数据处理
        List<FitPointBoard> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return CollUtils.emptyList();
        }
        records.forEach(b -> b.setRank(b.getId().intValue()));
        return records;
    }

    @Override
    public List<FitPointBoard> queryCurrentBoardList(String key, Integer pageNo, Integer pageSize) {
        // 1.计算分页
        int from = (pageNo - 1) * pageSize;
        // 2.查询
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, from, from + pageSize - 1);
        if (CollUtils.isEmpty(tuples)) {
            return CollUtils.emptyList();
        }
        // 3.封装
        int rank = from + 1;
        List<FitPointBoard> list = new ArrayList<>(tuples.size());
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            String userId = tuple.getValue();
            Double fitpoints = tuple.getScore();
            if (userId == null || fitpoints == null) {
                continue;
            }
            FitPointBoard p = new FitPointBoard();
            p.setUserId(Long.valueOf(userId));
            p.setFitPoint(fitpoints.intValue());
            p.setRank(rank++);
            list.add(p);
        }
        return list;
    }

    private FitPointBoard queryMyHistoryBoard(Long season) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();
        // 2.计算表名
        TableInfoContext.setInfo(FITPOINTS_BOARD_TABLE_PREFIX + season);
        // 3.查询数据
        Optional<FitPointBoard> opt = lambdaQuery().eq(FitPointBoard::getUserId, userId).oneOpt();
        if (opt.isEmpty()) {
            return null;
        }
        // 4.转换数据
        FitPointBoard fitpointsBoard = opt.get();
        fitpointsBoard.setRank(fitpointsBoard.getId().intValue());
        return fitpointsBoard;
    }

    private FitPointBoard queryMyCurrentBoard(String key) {
        // 1.绑定key
        BoundZSetOperations<String, String> ops = redisTemplate.boundZSetOps(key);
        // 2.获取当前用户信息
        String userId = UserContext.getUser().toString();
        // 3.查询积分
        Double fitpoints = ops.score(userId);
        // 4.查询排名
        Long rank = ops.reverseRank(userId);
        // 5.封装返回
        FitPointBoard p = new FitPointBoard();
        p.setFitPoint(fitpoints == null ? 0 : fitpoints.intValue());
        p.setRank(rank == null ? 0 : rank.intValue() + 1);
        return p;
    }
}

