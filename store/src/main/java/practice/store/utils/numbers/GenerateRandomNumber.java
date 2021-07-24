package practice.store.utils.numbers;

import org.springframework.stereotype.Component;
import practice.store.exceptions.common.ListIsEmptyException;

import java.util.Random;

@Component
public class GenerateRandomNumber {


    public Integer generateRandomIndexFromListSize(int listSize) {
        if (listSize == 0)
            throw new ListIsEmptyException();
        return new Random().nextInt(listSize);
    }
}
