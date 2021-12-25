package gateway.cmcc.com.service;

import gateway.cmcc.com.domain.SmsTask;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-24
 */
public interface MessageService {

    Boolean sendSms(SmsTask smsTask);

    Boolean sendSmsInner(SmsTask smsTask);
}
