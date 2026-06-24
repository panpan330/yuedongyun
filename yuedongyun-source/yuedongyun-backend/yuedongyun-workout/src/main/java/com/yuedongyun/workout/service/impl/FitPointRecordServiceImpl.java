package com.yuedongyun.workout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.common.utils.DateUtils;
import com.yuedongyun.common.utils.UserContext;
import com.yuedongyun.workout.constants.RedisConstants;
import com.yuedongyun.workout.domain.po.FitPointRecord;
import com.yuedongyun.workout.domain.vo.FitPointStatisticsVO;
import com.yuedongyun.workout.enums.FitPointRecordType;
import com.yuedongyun.workout.mapper.FitPointRecordMapper;
import com.yuedongyun.workout.service.IFitPointRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 跟练积分记录，每个月底清零 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Service
@RequiredArgsConstructor
public class FitPointRecordServiceImpl extends ServiceImpl<FitPointRecordMapper, FitPointRecord> implements IFitPointRecordService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void addFitPointRecord(Long userId, int fitpoints, FitPointRecordType type) {
        LocalDateTime now = LocalDateTime.now();
        int maxFitPoint = type.getMaxFitPoint();
        // 1.判断当前方式有没有积分上限
        int realFitPoint = fitpoints;
        if(maxFitPoint > 0) {
            // 2.有，则需要判断是否超过上限
            LocalDateTime begin = DateUtils.getDayStartTime(now);
            LocalDateTime end = DateUtils.getDayEndTime(now);
            // 2.1.查询今日已得积分
            int currentFitPoint = queryUserFitPointByTypeAndDate(userId, type, begin, end);
            // 2.2.判断是否超过上限
            if(currentFitPoint >= maxFitPoint) {
                // 2.3.超过，直接结束
                return;
            }
            // 2.4.没超过，保存积分记录
            if(currentFitPoint + fitpoints > maxFitPoint){
                realFitPoint = maxFitPoint - currentFitPoint;
            }
        }
        // 3.没有，直接保存积分记录
        FitPointRecord p = new FitPointRecord();
        p.setFitPoint(realFitPoint);
        p.setUserId(userId);
        p.setType(type);
        save(p);
        // 4.更新总积分到Redis
        String key = RedisConstants.FITPOINTS_BOARD_KEY_PREFIX + now.format(DateUtils.FITPOINTS_BOARD_SUFFIX_FORMATTER);
        redisTemplate.opsForZSet().incrementScore(key, userId.toString(), realFitPoint);
    }

    @Override
    public List<FitPointStatisticsVO> queryMyFitPointToday() {
        // 1.获取用户
        Long userId = UserContext.getUser();
        // 2.获取日期
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime begin = DateUtils.getDayStartTime(now);
        LocalDateTime end = DateUtils.getDayEndTime(now);
        // 3.构建查询条件
        QueryWrapper<FitPointRecord> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(FitPointRecord::getUserId, userId)
                .between(FitPointRecord::getCreateTime, begin, end);
        // 4.查询
        List<FitPointRecord> list = getBaseMapper().queryUserFitPointByDate(wrapper);
        if (CollUtils.isEmpty(list)) {
            return CollUtils.emptyList();
        }
        // 5.封装返回
        List<FitPointStatisticsVO> vos = new ArrayList<>(list.size());
        for (FitPointRecord p : list) {
            FitPointStatisticsVO vo = new FitPointStatisticsVO();
            vo.setType(p.getType().getDesc());
            vo.setMaxFitPoint(p.getType().getMaxFitPoint());
            vo.setFitPoint(p.getFitPoint());
            vos.add(vo);
        }
        return vos;
    }

    private int queryUserFitPointByTypeAndDate(
            Long userId, FitPointRecordType type, LocalDateTime begin, LocalDateTime end) {
        // 1.查询条件
        QueryWrapper<FitPointRecord> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(FitPointRecord::getUserId, userId)
                .eq(type != null, FitPointRecord::getType, type)
                .between(begin != null && end != null, FitPointRecord::getCreateTime, begin, end);
        // 2.调用mapper，查询结果
        Integer fitpoints = getBaseMapper().queryUserFitPointByTypeAndDate(wrapper);
        // 3.判断并返回
        return fitpoints == null ? 0 : fitpoints;
    }
}

