package gateway.cmcc.com.schedule;

import com.alibaba.fastjson.JSON;
import gateway.cmcc.com.constant.RedisKey;
import gateway.cmcc.com.domain.SmsTask;
import gateway.cmcc.com.service.MessageService;
import gateway.cmcc.com.support.TaskContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-25
 */
@Component
@Slf4j
public class SmsTaskSchedule implements ApplicationRunner {


    @Autowired
    TaskContainer taskContainer;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    MessageService messageService;

    private int stop = 0;

    /**
     * 每秒从redis里读任务,逻辑还有问题
     * TODO:
     */
    @Scheduled(fixedRate = 1000L)
    public void seek() {
        // 当当前任务小于3的时候才读
        Integer queueSize = taskContainer.getQueueSize();
        if (queueSize < 3 && stop-- <= 0) {
            ZSetOperations<String, String> setOperations = redisTemplate.opsForZSet();
            Set<String> range = setOperations.range(RedisKey.SMS_TASK_QUEUE, 0, 9);
            log.info("定时器获取新任务数:{}", Objects.nonNull(range) ? range.size() : 0);
            if (!CollectionUtils.isEmpty(range)) {
                HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
                List<Object> a = new ArrayList<>(range);
                List<Object> objects = opsForHash.multiGet(RedisKey.SMS_TASK_DETAIL, a);
                if (!CollectionUtils.isEmpty(objects)) {
                    List<SmsTask> smsTaskList = objects.stream()
                        .filter(Objects::nonNull)
                        .map(v -> JSON.parseObject(v.toString(), SmsTask.class))
                        .collect(Collectors.toList());
                    taskContainer.pullAll(smsTaskList);
                }
            }else {
                // 暂停10秒
                stop = 10;
            }
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 死循环来执行，本地缓存的短信
        while (true) {
            log.info("定时器正在运行,队列数为:{}", taskContainer.getQueueSize());
            SmsTask take = taskContainer.take();
            Boolean aBoolean = messageService.sendSmsInner(take);
            log.info("消费结果：" + aBoolean + "；当前队列长度:" + taskContainer.getQueueSize());
            if (aBoolean) {
                taskContainer.removeFromRedis(take);
            }else {
                taskContainer.addLocal(take);
                taskContainer.deleteExec(take);
                Thread.sleep(100);
            }
        }
    }
}
