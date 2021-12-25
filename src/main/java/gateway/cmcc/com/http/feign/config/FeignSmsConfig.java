package gateway.cmcc.com.http.feign.config;

import feign.*;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-24
 */
public class FeignSmsConfig {

    public int connectTimeout = 5000; //5s 超时时间
    public int readTimeout = 60000;  //60s

    /**
     * 配置 feign 日志打印级别: 默认打印所有
     * @return
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * 配置 feign 超时时间: 5s, 读取: 60s
     * @return
     */
    @Bean
    public Request.Options options() {
        return new Request.Options(connectTimeout, TimeUnit.MILLISECONDS, readTimeout, TimeUnit.MILLISECONDS, false);
    }

    /**
     * 连接超时重试策略: 不重试
     * @return
     */
    @Bean
    public Retryer feignRetry() {
        return Retryer.NEVER_RETRY;
    }


    @Bean
    public RequestInterceptor feignInterceptor () {

        return new RequestInterceptor() {
            public void apply(RequestTemplate template) {
                // TODO: with token
            }
        };
    }
}
