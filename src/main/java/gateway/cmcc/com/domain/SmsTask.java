package gateway.cmcc.com.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-25
 */
@Setter
@Getter
@ToString
public class SmsTask {

    /**
     * token
     */
    private String sessionId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 优先级
     */
    private String qos;

    /**
     * 手机号
     */
    private String tels;

    /**
     * 任务id, 后端生成
     */
    private String taskId;

    /**
     * 短信标题
     */
    private String title;

    /**
     * 短信内容
     */
    private String content;

}
