package practice.store.utils.values;

import org.springframework.stereotype.Component;

@Component
public class RandomStringGenerator {

    public String generateRandomUuid() {
        return java.util.UUID.randomUUID().toString();
    }
}
