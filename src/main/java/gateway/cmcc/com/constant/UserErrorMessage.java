package gateway.cmcc.com.constant;

import lombok.Getter;
import lombok.ToString;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-25
 */
@Getter
@ToString
public enum  UserErrorMessage {

    // TODO: 业务错误
    ;


    private String message;

    private Integer code;

    UserErrorMessage(String message, Integer code) {
        this.message = message;
        this.code = code;
    }


}
