package gateway.cmcc.com.constant;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-25
 */
public interface RedisKey {


    /**  sms **/

    String PREFIX = "";

    String SMS_TASK_QUEUE = "SMS:TASK:QUEUE";

    String SMS_TASK_DETAIL = "SMS:TASK:DETAIL";

    String SMS_PER_MOBILE = "SMS:PER:MOBILE:";

    String SMS_PER_REQUEST = "SMS:PER:REQUEST:";

    String SMS_SENT_TASK = "SMS:SENT:TASK:";

    String SMS_SENT_SET = "SMS:SENT:SET";


    /**  user **/

    /**
     * 存放用户信息
     */
    String USER_REGISTER_MAP = "USER:REGISTER:MAP";

    /**
     * 抢注册
     */
    String USER_REGISTER_SET = "USER:REGISTER:SET";

    /**
     * 用户存放token的地方
     */
    String USER_TOKEN_MAP = "USER:TOKEN:MAP";


    default String getMobileKey(String mobile) {
        return SMS_PER_MOBILE + mobile;
    }

    default String getSmsSentTask(String taskId) {
        return SMS_SENT_TASK + taskId;
    }
}
