package practice.store.utils.numbers;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import practice.store.exceptions.common.ListIsEmptyException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@DisplayName("Test generate random number")
class GenerateRandomNumberTest {

    private GenerateRandomNumber generateRandomNumber;


    @BeforeEach
    void setUp() {
        generateRandomNumber = new GenerateRandomNumber();
    }


    @DisplayName("Generate random number based on list size when list is not empty")
    @Test
    void should_generate_random_number_when_list_is_not_empty_test() {
        // given
        int listSize = 10;


        // when
        int randomNumber = generateRandomNumber.generateRandomIndexFromListSize(listSize);


        // then
        assertThat(randomNumber).isBetween(0, listSize);
    }

    @DisplayName("Throw exception when list is empty")
    @Test
    void should_throw_exception_when_list_is_empty_test() {
        // given
        int listSize = 0;


        // when
        Throwable exception = catchThrowable(() -> generateRandomNumber.generateRandomIndexFromListSize(listSize));


        // then
        Assertions.assertThat(exception)
                .isInstanceOf(ListIsEmptyException.class)
                .hasMessageContaining("The list is empty. Please try later.");
    }
}