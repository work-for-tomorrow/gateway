package gateway.cmcc.com.domain.dto;

import gateway.cmcc.com.domain.SmsTask;
import gateway.cmcc.com.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author jason
 * @version 1.0
 * @date 2021-12-25
 */
@Getter
@Setter
@ToString
public class SmsBodyDto {

    /**
     * 消息优先级
     */
    private String qos;

    /**
     * 手机号码
     */
    private String acceptor_tel;

    /**
     * 发送内存
     */
    private SmsContentObj template_param;

    /**
     * 发送时间
     */
    private String timestamp;


    public static SmsBodyDto build(SmsTask smsTask) {

        SmsBodyDto smsBodyDto = new SmsBodyDto();
        smsBodyDto.setQos(smsTask.getQos());
        smsBodyDto.setAcceptor_tel(smsTask.getTels());

        SmsContentObj smsContentObj = new SmsContentObj();
        smsContentObj.setContent(smsTask.getContent());
        smsContentObj.setTitle(smsTask.getTitle());
        smsBodyDto.setTemplate_param(smsContentObj);

        smsBodyDto.setTimestamp(DateUtil.getCurrentTimeString());

        return smsBodyDto;
    }
}
