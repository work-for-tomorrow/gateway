package gateway.cmcc.com.service.impl;

import gateway.cmcc.com.config.LimiterConfig;
import gateway.cmcc.com.constant.RedisKey;
import gateway.cmcc.com.service.LimiterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-25
 */
@Service(value = "gatewayLimiterService")
@Slf4j
public class LimiterServiceImpl implements LimiterService, RedisKey {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private LimiterConfig limiterConfig;

    @Override
    public Boolean getTicket(String key) {
        ValueOperations<String, String> forValue = redisTemplate.opsForValue();
        Boolean setIfAbsent = forValue.setIfAbsent(getMobileKey(key), "1", limiterConfig.getPerMobile(), TimeUnit.MILLISECONDS);
        log.debug("获取单独手机锁结果:{},锁key: {}", setIfAbsent, getMobileKey(key));
        if (null != setIfAbsent && !setIfAbsent) {
            return false;
        }
        String script = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then\n" +
            "    redis.call('pexpire', KEYS[1], ARGV[2])\n" +
            "    return 1\n" +
            "elseif tonumber(redis.call('get', KEYS[1])) > 0 then\n" +
            "        redis.call('INCRBY', KEYS[1], -1)\n" +
            "        return 1\n" +
            "else \n" +
            "    return 0\n" +
            "end";
        String sha1 = DigestUtils.sha1DigestAsHex(script);

        String k = RedisKey.SMS_PER_REQUEST + System.currentTimeMillis() / 2000;
        long ttl = 1000L * 2;
        Long execute = redisTemplate.execute(new RedisScript<Long>() {
            @Override
            public String getSha1() {
                return sha1;
            }

            @Override
            public Class<Long> getResultType() {
                return Long.class;
            }

            @Override
            public String getScriptAsString() {
                return script;
            }
        }, Collections.singletonList(k), String.valueOf(limiterConfig.getPerRequest() - 1), String.valueOf(ttl));
        log.debug("获取限流ticket结果:{};锁key为:{}", execute, k);
        return execute != null && execute > 0;
    }

}
