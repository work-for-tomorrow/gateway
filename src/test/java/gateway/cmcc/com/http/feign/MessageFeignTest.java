package gateway.cmcc.com.http.feign;

import gateway.cmcc.com.constant.MessageRoute;
import gateway.cmcc.com.domain.Response;
import gateway.cmcc.com.domain.SmsTask;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-26
 */
@FeignClient(url = "${local.request-url}", name = "messageFeignTest")
public interface MessageFeignTest {


    @PostMapping(value = MessageRoute.DIRECT_MESSAGE)
    Response directMessage(@RequestParam(value = "sessionId", required = false) String sessionId,
                                  @RequestParam(value = "userName", required = false) String userName,
                                  @RequestParam(value = "qos", required = false) String qos,
                                  @RequestParam(value = "tels",required = false) String tels,
                                  @RequestBody SmsTask smsTask);
}
