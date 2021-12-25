package gateway.cmcc.com.service;

import gateway.cmcc.com.domain.dto.UserAddDto;
import gateway.cmcc.com.domain.dto.UserLogoutDto;
import gateway.cmcc.com.domain.dto.UserSmsMessageDto;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-24
 */
public interface UserService {


    Boolean register(UserAddDto userAddDto);

    String login(UserAddDto userAddDto);

    Boolean logout(UserLogoutDto userLogoutDto);

    Boolean checkToken(String userName, String token);
}
