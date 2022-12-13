package nl.tudelft.sem.waitinglist.profiles;

import java.time.Clock;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockClock")
@Configuration
public class MockClock {
    @Bean
    @Primary
    public Clock getClock() {
        return Mockito.mock(Clock.class);
    }
}
