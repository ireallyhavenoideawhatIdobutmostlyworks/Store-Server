package practice.store.validator;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
class LogHelper {

    boolean logIfFalse(boolean result, String desc) {
        if (!result) {
            log.error("Result condition is false because: {}.", desc);
            return false;
        }
        return true;
    }
}
