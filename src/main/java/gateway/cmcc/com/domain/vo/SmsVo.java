package gateway.cmcc.com.domain.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-24
 */
@Getter
@Setter
@ToString
public class SmsVo {

    private String res_code;

    private String res_message;


    public Boolean isSuccess() {
        return "0".equals(res_code);
    }

}
