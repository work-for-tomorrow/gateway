package gateway.cmcc.com.domain.dto;

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
public class UserLogoutDto {

    /**
     * 用户名
     */
    private String userName;

    /**
     * token
     */
    private String sessionId;
}
