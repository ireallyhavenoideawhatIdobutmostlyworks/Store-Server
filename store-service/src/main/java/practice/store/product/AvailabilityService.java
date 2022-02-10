package practice.store.product;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
final class AvailabilityService {

    public Availability calculateAvailability(String uuid, int amount) {
        if (amount == 0) {
            log.info("Amount of product with UUID:{} is 0. Set availability to: {}", uuid, Availability.NOT_AVAILABLE);
            return Availability.NOT_AVAILABLE;
        }

        if (amount < 5) {
            log.info("Amount of product with UUID:{} is less than 5. Set availability to: {}", uuid, Availability.AWAITING_FROM_MANUFACTURE);
            return Availability.AWAITING_FROM_MANUFACTURE;
        }

        log.info("Amount of product with UUID:{} is {}. Set availability to: {}", uuid, amount, Availability.AVAILABLE);
        return Availability.AVAILABLE;
    }
}
