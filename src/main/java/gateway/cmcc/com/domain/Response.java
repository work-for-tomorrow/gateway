package gateway.cmcc.com.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-24
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Response<T> {

    private T sessionId;

    private Integer code;

    private String Message;

    public static <T> Response<T> ok() {
        return ok(null);
    }

    public static <T> Response<T> ok(T data) {
       return ok(data, "success");
    }


    public static <T> Response<T> ok(T data, String msg) {
       return new Response<>(data, 200, msg);
    }


    public static <T> Response<T> error(String msg) {
        return error(msg, 400);
    }

    public static <T> Response<T> error(String msg, Integer code) {
        return new Response<>(null, code, msg);
    }

}
