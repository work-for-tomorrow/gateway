package gateway.cmcc.com.http.feign;

import gateway.cmcc.com.constant.SmsRoute;
import gateway.cmcc.com.domain.dto.SmsBodyDto;
import gateway.cmcc.com.domain.vo.SmsVo;
import gateway.cmcc.com.http.feign.config.FeignSmsConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-24
 */
@FeignClient(url = "${sms.address:http://42.194.133.29:8090}", configuration = FeignSmsConfig.class, name = "sms")
@RequestMapping(value = SmsRoute.BASE)
public interface SmsFeign {

    @PostMapping(value = SmsRoute.SEND_MESSAGE)
    SmsVo sendSms(@RequestBody SmsBodyDto smsBodyDto);
}
