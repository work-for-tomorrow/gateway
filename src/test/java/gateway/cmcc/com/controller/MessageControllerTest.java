package gateway.cmcc.com.controller;

import gateway.cmcc.com.BaseTest;
import gateway.cmcc.com.domain.SmsTask;
import gateway.cmcc.com.http.feign.MessageFeignTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-26
 */

public class MessageControllerTest extends BaseTest {

    @Autowired
    private MessageFeignTest messageFeignTest;

    @Test
    public void messageTest() {
        // 39a362f4-efd5-43b6-823f-245e92c1ae1c
        String sessionId = "39a362f4-efd5-43b6-823f-245e92c1ae1c";
        String userName = "jasonZeng";

        SmsTask smsTask = new SmsTask();
        smsTask.setTitle("test");
        smsTask.setContent("test-content-1");

        LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(1000);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(15, 15, 60, TimeUnit.SECONDS, blockingQueue);
        for (int j = 0; j < 1000; j++) {
            Random random = new Random();
            int i = random.nextInt(3) + 1;
            String qos = String.valueOf(i);
            String tels = "18930" + j;
            poolExecutor.submit(() -> {
                messageFeignTest.directMessage(
                    sessionId,
                    userName,
                    qos,
                    tels,
                    smsTask
                );
            });
        }
        poolExecutor.shutdown();
        while (poolExecutor.getActiveCount() != 0) {

        }
    }

}
