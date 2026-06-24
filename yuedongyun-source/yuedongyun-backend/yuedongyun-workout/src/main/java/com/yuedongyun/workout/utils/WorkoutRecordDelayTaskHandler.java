package com.yuedongyun.workout.utils;

import com.yuedongyun.common.utils.JsonUtils;
import com.yuedongyun.common.utils.StringUtils;
import com.yuedongyun.workout.domain.po.WorkoutSession;
import com.yuedongyun.workout.domain.po.WorkoutRecord;
import com.yuedongyun.workout.mapper.WorkoutRecordMapper;
import com.yuedongyun.workout.service.IWorkoutSessionService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.DelayQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkoutRecordDelayTaskHandler {

    private final StringRedisTemplate redisTemplate;
    private final WorkoutRecordMapper recordMapper;
    private final IWorkoutSessionService sessionService;
    private final DelayQueue<DelayTask<RecordTaskData>> queue = new DelayQueue<>();
    private final static String RECORD_KEY_TEMPLATE = "workout:record:{}";
    private static volatile boolean begin = true;

    @PostConstruct
    public void init(){
        CompletableFuture.runAsync(this::handleDelayTask);
    }
    @PreDestroy
    public void destroy(){
        begin = false;
        log.debug("延迟任务停止执行！");
    }

    public void handleDelayTask(){
        while (begin) {
            try {
                // 1.获取到期的延迟任务
                DelayTask<RecordTaskData> task = queue.take();
                RecordTaskData data = task.getData();
                // 2.查询Redis缓存
                WorkoutRecord record = readRecordCache(data.getSessionId(), data.getSessionId());
                if (record == null) {
                    continue;
                }
                // 3.比较数据，moment值
                if(!Objects.equals(data.getMoment(), record.getMoment())) {
                    // 不一致，说明用户还在持续提交播放进度，放弃旧数据
                    continue;
                }

                // 4.一致，持久化播放进度数据到数据库
                // 4.1.更新跟练记录的moment
                record.setFinished(null);
                recordMapper.updateById(record);
                // 4.2.更新课表最近跟练信息
                WorkoutSession session = new WorkoutSession();
                session.setId(data.getSessionId());
                session.setLatestSessionId(data.getSessionId());
                session.setLatestLearnTime(LocalDateTime.now());
                sessionService.updateById(session);
            } catch (Exception e) {
                log.error("处理延迟任务发生异常", e);
            }
        }
    }

    public void addWorkoutRecordTask(WorkoutRecord record){
        // 1.添加数据到Redis缓存
        writeRecordCache(record);
        // 2.提交延迟任务到延迟队列 DelayQueue
        queue.add(new DelayTask<>(new RecordTaskData(record), Duration.ofSeconds(20)));
    }

    public void writeRecordCache(WorkoutRecord record) {
        log.debug("更新跟练记录的缓存数据");
        try {
            // 1.数据转换
            String json = JsonUtils.toJsonStr(new RecordCacheData(record));
            // 2.写入Redis
            String key = StringUtils.format(RECORD_KEY_TEMPLATE, record.getSessionId());
            redisTemplate.opsForHash().put(key, record.getSessionId().toString(), json);
            // 3.添加缓存过期时间
            redisTemplate.expire(key, Duration.ofMinutes(1));
        } catch (Exception e) {
            log.error("更新跟练记录缓存异常", e);
        }
    }

    public WorkoutRecord readRecordCache(Long sessionId, Long sessionId){
        try {
            // 1.读取Redis数据
            String key = StringUtils.format(RECORD_KEY_TEMPLATE, sessionId);
            Object cacheData = redisTemplate.opsForHash().get(key, sessionId.toString());
            if (cacheData == null) {
                return null;
            }
            // 2.数据检查和转换
            return JsonUtils.toBean(cacheData.toString(), WorkoutRecord.class);
        } catch (Exception e) {
            log.error("缓存读取异常", e);
            return null;
        }
    }

    public void cleanRecordCache(Long sessionId, Long sessionId){
        // 删除数据
        String key = StringUtils.format(RECORD_KEY_TEMPLATE, sessionId);
        redisTemplate.opsForHash().delete(key, sessionId.toString());
    }

    @Data
    @NoArgsConstructor
    private static class RecordCacheData{
        private Long id;
        private Integer moment;
        private Boolean finished;

        public RecordCacheData(WorkoutRecord record) {
            this.id = record.getId();
            this.moment = record.getMoment();
            this.finished = record.getFinished();
        }
    }
    @Data
    @NoArgsConstructor
    private static class RecordTaskData{
        private Long sessionId;
        private Long sessionId;
        private Integer moment;

        public RecordTaskData(WorkoutRecord record) {
            this.sessionId = record.getSessionId();
            this.sessionId = record.getSessionId();
            this.moment = record.getMoment();
        }
    }
}

