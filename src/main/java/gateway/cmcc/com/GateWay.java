package gateway.cmcc.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-24
 */
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class GateWay {

    public static void main(String[] args) {
        SpringApplication.run(GateWay.class, args);
    }
}
