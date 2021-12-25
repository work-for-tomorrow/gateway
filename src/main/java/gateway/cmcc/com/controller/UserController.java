package gateway.cmcc.com.controller;

import gateway.cmcc.com.constant.ErrorMessage;
import gateway.cmcc.com.constant.UserRoute;
import gateway.cmcc.com.domain.Response;
import gateway.cmcc.com.domain.dto.UserAddDto;
import gateway.cmcc.com.domain.dto.UserLogoutDto;
import gateway.cmcc.com.domain.dto.UserSmsMessageDto;
import gateway.cmcc.com.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-24
 */
@RestController
@RequestMapping(value = UserRoute.BASE)
@Slf4j
public class UserController {


    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * @param userAddDto 注册传输数据
     * @return 结果，成功带token，失败返回错误信息
     */
    @PostMapping(value = UserRoute.REGISTER)
    public Response<String> register(@RequestBody UserAddDto userAddDto) {
        // check param
        if (this.checkUserParam(userAddDto)) {
            return Response.error(ErrorMessage.PARAM_ERROR.getMessage(), ErrorMessage.PARAM_ERROR.getCode());
        }
        // 这里会注册失败
        Boolean register = userService.register(userAddDto);
        if (!register) {
            // 抢注册失败
            return Response.error(ErrorMessage.PARAM_ERROR.getMessage(), ErrorMessage.PARAM_ERROR.getCode());
        }
        return Response.ok();
    }

    /**
     * 用户登录，跟注册带逻辑差不多，少了验证
     * @param userAddDto 登录传输的数据
     * @return 结果，成功返回token，失败返回原因
     */
    @PostMapping(value = UserRoute.LOGIN)
    public Response<String> login(@RequestBody UserAddDto userAddDto) {
        // check param
        if (this.checkUserParam(userAddDto)) {
            return Response.error(ErrorMessage.PARAM_ERROR.getMessage(), ErrorMessage.PARAM_ERROR.getCode());
        }
        String login = userService.login(userAddDto);
        // 当返回的token为null，则表示登录失败
        if (StringUtils.isEmpty(login)) {
            // 这里应该返回密码错误的，但是文档没提，所以统一返回参数错误
            return Response.error(ErrorMessage.PARAM_ERROR.getMessage(), ErrorMessage.PARAM_ERROR.getCode());
        }
        return Response.ok(login);
    }


    /**
     * 用户登出，销毁session
     * @param userLogoutDto 登录传输对象
     * @return 返回结果
     */
    @PostMapping(value = UserRoute.LOGOUT)
    public Response<Boolean> logout(@RequestBody UserLogoutDto userLogoutDto) {
        // check param
        if (userLogoutDto == null || StringUtils.isEmpty(userLogoutDto.getSessionId()) || StringUtils.isEmpty(userLogoutDto.getUserName())) {
            return Response.error(ErrorMessage.PARAM_ERROR.getMessage(), ErrorMessage.PARAM_ERROR.getCode());
        }
        //
        Boolean logout = userService.logout(userLogoutDto);
        if (!logout) {
            // 这里会检验密码token是否正确
            return Response.error(ErrorMessage.PARAM_ERROR.getMessage(), ErrorMessage.PARAM_ERROR.getCode());
        }
        return Response.ok();
    }


    /**
     * 	string
     * minLength: 3
     * maxLength: 32
     * example: admin
     * 用户登录名，只允许数字，大小写字母。
     *
     * password*	string
     * minLength: 8
     * maxLength: 64
     * example: Adminpassword123
     * @param userAddDto
     * @return
     */
    private Boolean checkUserParam(UserAddDto userAddDto) {
        boolean base = userAddDto == null || StringUtils.isEmpty(userAddDto.getPassword()) || StringUtils.isEmpty(userAddDto.getUserName());
        if (!base) {
            String name = userAddDto.getUserName();
            String pass = userAddDto.getPassword();
            boolean userName = name.length() < 3 || name.length() > 32 || checkUserName(name);
            if (userName) {
                return true;
            }
            return pass.length() < 8 || pass.length() > 64 || checkUserName(pass);
        }
        return true;
    }

    private Boolean checkUserName(String userName) {

        for (int i = 0; i < userName.length(); i++) {
            char tmp = userName.charAt(i);
            // 48～57 数字
            // 65～90 大写字母
            // 97～122 小写字母
            if (tmp < 48 || (tmp > 57 && tmp < 65) || (tmp > 90 && tmp < 97) || tmp > 122) {
                return true;
            }
        }
        return false;
    }

}

