package gateway.cmcc.com.controller;

import cn.hutool.core.lang.UUID;
import gateway.cmcc.com.constant.ErrorMessage;
import gateway.cmcc.com.constant.MessageRoute;
import gateway.cmcc.com.domain.Response;
import gateway.cmcc.com.domain.SmsTask;
import gateway.cmcc.com.service.MessageService;
import gateway.cmcc.com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-25
 */
@RestController
public class MessageController {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @PostMapping(value = MessageRoute.DIRECT_MESSAGE)
    public Response directMessage(@RequestParam(value = "sessionId", required = false) String sessionId,
                                  @RequestParam(value = "userName", required = false) String userName,
                                  @RequestParam(value = "qos", required = false) String qos,
                                  @RequestParam(value = "tels",required = false) String tels,
                                  @RequestBody SmsTask smsTask) {

        if (StringUtils.isEmpty(sessionId)
            || StringUtils.isEmpty(userName)
            || StringUtils.isEmpty(qos)
            || StringUtils.isEmpty(tels)
            || Objects.isNull(smsTask)
            || checkSmsContent(smsTask)
        ) {
            return Response.error(ErrorMessage.PARAM_ERROR.getMessage(), ErrorMessage.PARAM_ERROR.getCode());
        }
        // 密码错误
        if (!userService.checkToken(userName, sessionId)){
            return Response.error(ErrorMessage.UNAUTHORIZED.getMessage(), ErrorMessage.UNAUTHORIZED.getCode());
        }

        String taskId = UUID.randomUUID().toString(true);
        smsTask.setTaskId(taskId);
        smsTask.setSessionId(sessionId);
        smsTask.setUserName(userName);
        smsTask.setQos(qos);
        smsTask.setTels(tels);
        return Response.ok(messageService.sendSms(smsTask));
    }

    private Boolean checkSmsContent(SmsTask smsTask) {
        if (Objects.isNull(smsTask.getContent()) || Objects.isNull(smsTask.getTitle())) {
            return true;
        }
        return smsTask.getTitle().length() < 1 || smsTask.getTitle().length() > 64;
    }
}
