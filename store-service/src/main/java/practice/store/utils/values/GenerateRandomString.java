package practice.store.utils.values;

import org.springframework.stereotype.Component;

@Component
public class GenerateRandomString {

    public String generateRandomUuid() {
        return java.util.UUID.randomUUID().toString();
    }
}
