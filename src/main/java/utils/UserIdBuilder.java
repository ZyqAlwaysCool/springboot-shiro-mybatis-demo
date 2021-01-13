package utils;

import com.zyq.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class UserIdBuilder {
    private static Logger log = LoggerFactory.getLogger(UserIdBuilder.class);
    public static String getUUID(){
        log.info("UserIdBuilder=>getUUID()");
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
