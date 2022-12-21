package profiles;

import nl.tudelft.sem.common.models.providers.TimeProvider;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockTime")
@Configuration
public class MockTimeProvider {

    @Bean
    @Primary
    public TimeProvider getTimeProvider() {
        return Mockito.mock(TimeProvider.class);
    }
}
