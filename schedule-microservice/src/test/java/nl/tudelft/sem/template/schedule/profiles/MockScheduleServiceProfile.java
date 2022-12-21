package nl.tudelft.sem.template.schedule.profiles;

import nl.tudelft.sem.common.models.providers.TimeProvider;
import nl.tudelft.sem.template.schedule.domain.request.ScheduleService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockScheduleService")
@Configuration
public class MockScheduleServiceProfile {

    @Bean
    @Primary
    public ScheduleService getScheduleService() {
        return Mockito.mock(ScheduleService.class);
    }
}
