package gateway.cmcc.com.service.impl;

import cn.hutool.core.lang.UUID;
import gateway.cmcc.com.constant.RedisKey;
import gateway.cmcc.com.domain.dto.UserAddDto;
import gateway.cmcc.com.domain.dto.UserLogoutDto;
import gateway.cmcc.com.domain.dto.UserSmsMessageDto;
import gateway.cmcc.com.service.UserService;
import gateway.cmcc.com.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-24
 */
@Service("gatewayUserService")
public class UserServiceImpl implements UserService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    public Boolean register(UserAddDto userAddDto) {

        // 抢注册
        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
        Long add = opsForSet.add(RedisKey.USER_REGISTER_SET, userAddDto.getUserName());
        if (add != null && add > 0) {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            // 注册成功
            hashOperations.put(RedisKey.USER_REGISTER_MAP, userAddDto.getUserName(), userAddDto.getPassword());
            return true;
        }
        return false;
    }

    public String login(UserAddDto userAddDto) {

        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        Object o = opsForHash.get(RedisKey.USER_REGISTER_MAP, userAddDto.getUserName());
        if (Objects.nonNull(o) && o.toString().equals(userAddDto.getPassword())) {
            String token = UUID.randomUUID().toString();
            // TODO: 这里有可能被覆盖, 可以考虑使用lua脚本保证操作的原子性，先拿出来，有的话直接返回，没有就设置
            opsForHash.put(RedisKey.USER_TOKEN_MAP, userAddDto.getUserName(), token);
            return token;
        }
        return null;
    }

    public Boolean logout(UserLogoutDto userLogoutDto) {

        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        if (SpringUtil.getBean(UserService.class).checkToken(userLogoutDto.getUserName(), userLogoutDto.getSessionId())){
            // 删除token
            opsForHash.delete(RedisKey.USER_TOKEN_MAP, userLogoutDto.getUserName());
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean checkToken(String userName, String token) {
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();

        Object o = opsForHash.get(RedisKey.USER_TOKEN_MAP, userName);
        if (Objects.nonNull(o) && o.toString().equals(token)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


}
