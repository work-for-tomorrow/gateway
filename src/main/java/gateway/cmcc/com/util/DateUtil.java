package gateway.cmcc.com.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author jason
 * @version 1.0
 * @date 2021-12-25
 */
public class DateUtil {


    public static String getCurrentTimeString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return localDateTime.format(dateTimeFormatter);
    }

    public static void main(String[] args) {
        System.out.println(getCurrentTimeString());
    }
}
