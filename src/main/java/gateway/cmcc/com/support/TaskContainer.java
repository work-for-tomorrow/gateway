package gateway.cmcc.com.support;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import gateway.cmcc.com.constant.RedisKey;
import gateway.cmcc.com.domain.SmsTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author jason
 * @version 1.0
 * @date 2021-12-25
 */
@Component
public class TaskContainer implements ApplicationRunner,RedisKey {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private PriorityBlockingQueue<SmsTask> priorityQueue;

    public TaskContainer() {
        Comparator<SmsTask> comparator = Comparator.comparing(SmsTask::getQos);
        priorityQueue = new PriorityBlockingQueue<>(1000,comparator);
    }

    public SmsTask take() throws InterruptedException {
        SmsTask take;
        while (true) {
           take =  priorityQueue.take();
           if (this.checkIfExec(take)) {
               return take;
           }
        }
    }


    /**
     * 此方法只加入本地缓存
     * @param smsTaskList 任务列表
     */
    public void pullAll(List<SmsTask> smsTaskList) {
        priorityQueue.addAll(smsTaskList);
    }

    /**
     * 此方法分别加入redis和本地
     * @param smsTask 任务
     */
    public void offer(SmsTask smsTask) {
        priorityQueue.add(smsTask);
        // 存储在redis中
        this.addToRedis(smsTask);
    }

    public void addLocal(SmsTask smsTask) {
        priorityQueue.add(smsTask);
    }

    /**
     *  检查是否有其他实例已经执行
     * @param smsTask 任务对象
     * @return 结果
     */
    public Boolean checkIfExec(SmsTask smsTask) {
        ValueOperations<String, String> forValue = redisTemplate.opsForValue();
        Boolean setIfAbsent = forValue.setIfAbsent(getSmsSentTask(smsTask.getTaskId()), "1", 60, TimeUnit.SECONDS);
        return setIfAbsent != null && setIfAbsent;
    }

    public void deleteExec(SmsTask smsTask) {
        redisTemplate.delete(getSmsSentTask(smsTask.getTaskId()));
    }

    /**
     * 存入redis
     * @param smsTask
     */
    public void addToRedis(SmsTask smsTask) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        zSetOperations.add(RedisKey.SMS_TASK_QUEUE, smsTask.getTaskId(), Double.valueOf(smsTask.getQos()));
        opsForHash.put(RedisKey.SMS_TASK_DETAIL, smsTask.getTaskId(), JSON.toJSONString(smsTask));
    }

    public void removeFromRedis(SmsTask smsTask) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        zSetOperations.remove(RedisKey.SMS_TASK_QUEUE, smsTask.getTaskId());
        opsForHash.delete(RedisKey.SMS_TASK_DETAIL, smsTask.getTaskId());
    }

    public Integer getQueueSize() {
        return this.priorityQueue.size();
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {

        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();

        List<Object> objectList = opsForHash.values(RedisKey.SMS_TASK_DETAIL);

        if (CollectionUtil.isNotEmpty(objectList)) {
            List<SmsTask> taskList = objectList.stream().filter(Objects::nonNull).map(v -> JSON.parseObject(v.toString(), SmsTask.class)).collect(Collectors.toList());
            priorityQueue.addAll(taskList);
        }

    }


}
