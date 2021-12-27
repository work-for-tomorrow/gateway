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
        long now = System.currentTimeMillis() + 4500L;
        // 获取门票
        Boolean ticket = limiterService.getTicket(smsTask.getTels());
        SmsBodyDto smsBodyDto = SmsBodyDto.build(smsTask);
        // 获取成功即可直接发送
        if (ticket) {
            if (sendSmsToServer(smsBodyDto)) return Boolean.TRUE;
        }else {
            if (smsTask.getQos().equals("1")) {
                while (now > System.currentTimeMillis()) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Boolean ticket1 = limiterService.getTicket(smsTask.getTels());
                    if (ticket1) {
                        if (sendSmsToServer(smsBodyDto)) {
                            return Boolean.TRUE;
                        }
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    private boolean sendSmsToServer(SmsBodyDto smsBodyDto) {
        try {
            SmsVo smsVo = smsFeign.sendSms(smsBodyDto);
            // 发送成功
            if (Objects.nonNull(smsVo) && smsVo.isSuccess()) {
                return true;
            }
        }catch (Exception e) {
            log.error("发送短信时发生错误:" + e.getMessage());
        }
        return false;
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
                }else {
                    return Boolean.TRUE;
                }
            }catch (Exception e) {
                opsForSet.remove(RedisKey.SMS_SENT_SET, smsTask.getTaskId());
                log.error("队列内发送短信时发生错误:" + e.getMessage());
            }
        }
        return Boolean.FALSE;
    }



}
