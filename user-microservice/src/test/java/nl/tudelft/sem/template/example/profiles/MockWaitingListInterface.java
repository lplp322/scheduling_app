package nl.tudelft.sem.template.example.profiles;

import nl.tudelft.sem.template.example.feigninterfaces.AuthenticationInterface;
import nl.tudelft.sem.template.example.feigninterfaces.WaitingListInterface;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockWaitingListInterface")
@Configuration
public class MockWaitingListInterface {
    /**
     * Mocks the WaitingListInterface.
     *
     * @return A mocked WaitingListInterface.
     */
    @Bean
    @Primary
    public WaitingListInterface getMockWaitingListInterface() {
        return Mockito.mock(WaitingListInterface.class);
    }
}
