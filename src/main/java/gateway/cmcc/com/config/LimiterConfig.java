package gateway.cmcc.com.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-25
 */
@Configuration
@Getter
public class LimiterConfig {

    /**
     * 每秒可发送多少个请求
     */
    @Value("${limiter.per.request:10}")
    private Integer perRequest;

    /**
     * 每个手机每秒可发送多少次
     */
    @Value("${limiter.per.mobile:1100}")
    private Integer perMobile;

}
