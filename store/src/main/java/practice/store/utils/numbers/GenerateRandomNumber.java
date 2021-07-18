package practice.store.utils.numbers;

import org.springframework.stereotype.Component;
import practice.store.exceptions.common.ListIsEmptyException;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class GenerateRandomNumber {

    public Integer generateRandomIntFromRange(int from, int to) {
        return ThreadLocalRandom.current().nextInt(from, to);
    }

    public Integer generateRandomIndexFromListSize(int listSize) {
        if (listSize == 0)
            throw new ListIsEmptyException();
        return new Random().nextInt(listSize);
    }
}
