package co.com.bancolombia.notification.usecase.test;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class NotificationUseCaseTest {

    @Test
    public void testUseCaseVoid() {

        String msgId = "Hello";
        // Assert
        Assertions.assertEquals("Hello", msgId);
    }
}
