package gateway.cmcc.com.service.impl;

import gateway.cmcc.com.constant.RedisKey;
import gateway.cmcc.com.domain.SmsTask;
import gateway.cmcc.com.domain.dto.SmsBodyDto;
import gateway.cmcc.com.domain.vo.SmsVo;
import gateway.cmcc.com.http.feign.SmsFeign;
import gateway.cmcc.com.service.LimiterService;
import gateway.cmcc.com.service.MessageService;
import gateway.cmcc.com.support.TaskContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-24
 */
@Service(value = "gatewayMessageService")
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private SmsFeign smsFeign;

    @Autowired
    private LimiterService limiterService;

    @Autowired
    private TaskContainer taskContainer;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public Boolean sendSms(SmsTask smsTask) {

        // 获取门票
        Boolean ticket = limiterService.getTicket(smsTask.getTels());

        // 获取成功即可直接发送
        if (ticket) {
            try {
                // 发送短信
                SmsVo smsVo = smsFeign.sendSms(SmsBodyDto.build(smsTask));
                // 发送成功
                if (Objects.nonNull(smsVo) && smsVo.isSuccess()) {
                    SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
                    opsForSet.add(RedisKey.SMS_SENT_SET, smsTask.getTaskId());
                    return Boolean.TRUE;
                }
                taskContainer.offer(smsTask);
            }catch (Exception e) {
                log.error("发送短信时发生错误:"  + e.getMessage());
                taskContainer.offer(smsTask);
            }
        }else {
            // 不成功即存入容器
            taskContainer.offer(smsTask);
        }
        return Boolean.TRUE;
    }

    public Boolean sendSmsInner(SmsTask smsTask) {
        // 获取门票
        Boolean ticket = limiterService.getTicket(smsTask.getTels());
        if (ticket) {
            SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
            try {
                Long add = opsForSet.add(RedisKey.SMS_SENT_SET, smsTask.getTaskId());
                if (Objects.nonNull(add) && add > 0) {
                    // 发送短信
                    SmsVo smsVo = smsFeign.sendSms(SmsBodyDto.build(smsTask));
                    // 发送成功
                    if (Objects.nonNull(smsVo) && smsVo.isSuccess()) {
                        return Boolean.TRUE;
                    }
                }
            }catch (Exception e) {
                opsForSet.remove(RedisKey.SMS_SENT_SET, smsTask.getTaskId());
                log.error("队列内发送短信时发生错误:" + e.getMessage());
            }
        }
        return Boolean.FALSE;
    }



}
