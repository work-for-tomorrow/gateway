package gateway.cmcc.com.controller.exception;

import gateway.cmcc.com.constant.ErrorMessage;
import gateway.cmcc.com.domain.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-25
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public Response normalException(Exception e) {
        log.error(e.getMessage());
        return Response.error(ErrorMessage.SERVER_ERROR.getMessage(), ErrorMessage.SERVER_ERROR.getCode());
    }
}
