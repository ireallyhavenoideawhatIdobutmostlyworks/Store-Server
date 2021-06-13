package practice.store.utils.values;

public class RandomStringGenerator {

    public static String generateRandomUuid() {
        return java.util.UUID.randomUUID().toString();
    }
}
