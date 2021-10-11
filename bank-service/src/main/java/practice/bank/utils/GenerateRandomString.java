package practice.bank.utils;

import org.springframework.stereotype.Component;

import static java.util.UUID.randomUUID;

@Component
public class GenerateRandomString {

    public String generateRandomUuid() {
        return randomUUID().toString();
    }
}
