package gateway.cmcc.com.constant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-25
 */
@Getter
@ToString
public enum  ErrorMessage {

    /**
     * 请求参数错误
     */
    PARAM_ERROR("参数错误", 400),
    /**
     * 未授权
     */
    UNAUTHORIZED("未授权", 403),
    /**
     * 服务器错误
     */
    SERVER_ERROR("内部服务器错误", 500)
    ;

    private String message;

    private Integer code;

    ErrorMessage(String message, Integer code) {
        this.message = message;
        this.code = code;
    }



}
