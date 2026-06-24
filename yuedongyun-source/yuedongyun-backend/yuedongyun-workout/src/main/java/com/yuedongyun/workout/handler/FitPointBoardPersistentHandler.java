package com.yuedongyun.workout.handler;

import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.common.utils.DateUtils;
import com.yuedongyun.workout.constants.RedisConstants;
import com.yuedongyun.workout.domain.po.FitPointBoard;
import com.yuedongyun.workout.service.IFitPointBoardSeasonService;
import com.yuedongyun.workout.service.IFitPointBoardService;
import com.yuedongyun.workout.utils.TableInfoContext;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.yuedongyun.workout.constants.WorkoutConstants.FITPOINTS_BOARD_TABLE_PREFIX;

@Component
@RequiredArgsConstructor
public class FitPointBoardPersistentHandler {

    private final IFitPointBoardSeasonService seasonService;

    private final IFitPointBoardService fitpointsBoardService;

    private final StringRedisTemplate redisTemplate;

    @XxlJob("createTableJob")
    public void createFitPointBoardTableOfLastSeason(){
        // 1.获取上月时间
        LocalDateTime time = LocalDateTime.now().minusMonths(1);
        // 2.查询赛季id
        Integer season = seasonService.querySeasonByTime(time);
        if (season == null) {
            // 赛季不存在
            return;
        }
        // 3.创建表
        fitpointsBoardService.createFitPointBoardTableBySeason(season);
    }

    @XxlJob("saveFitPointBoard2DB")
    public void saveFitPointBoard2DB(){
        // 1.获取上月时间
        LocalDateTime time = LocalDateTime.now().minusMonths(1);

        // 2.计算动态表名
        // 2.1.查询赛季信息
        Integer season = seasonService.querySeasonByTime(time);
        // 2.2.存入ThreadLocal
        TableInfoContext.setInfo(FITPOINTS_BOARD_TABLE_PREFIX + season);

        // 3.查询榜单数据
        // 3.1.拼接KEY
        String key = RedisConstants.FITPOINTS_BOARD_KEY_PREFIX + time.format(DateUtils.FITPOINTS_BOARD_SUFFIX_FORMATTER);
        // 3.2.查询数据
        int index = XxlJobHelper.getShardIndex();
        int total = XxlJobHelper.getShardTotal();
        int pageNo = index + 1;
        int pageSize = 10;
        while (true) {
            List<FitPointBoard> boardList = fitpointsBoardService.queryCurrentBoardList(key, pageNo, pageSize);
            if (CollUtils.isEmpty(boardList)) {
                break;
            }
            // 4.持久化到数据库
            // 4.1.把排名信息写入id
            boardList.forEach(b -> {
                b.setId(b.getRank().longValue());
                b.setRank(null);
            });
            // 4.2.持久化
            fitpointsBoardService.saveBatch(boardList);
            // 5.翻页
            pageNo+=total;
        }

        TableInfoContext.remove();
    }

    @XxlJob("clearFitPointBoardFromRedis")
    public void clearFitPointBoardFromRedis(){
        // 1.获取上月时间
        LocalDateTime time = LocalDateTime.now().minusMonths(1);
        // 2.计算key
        String key = RedisConstants.FITPOINTS_BOARD_KEY_PREFIX + time.format(DateUtils.FITPOINTS_BOARD_SUFFIX_FORMATTER);
        // 3.删除
        redisTemplate.unlink(key);
    }
}

