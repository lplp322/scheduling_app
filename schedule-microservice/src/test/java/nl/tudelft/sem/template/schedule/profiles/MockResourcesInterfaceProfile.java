package nl.tudelft.sem.template.schedule.profiles;

import nl.tudelft.sem.template.schedule.domain.request.ScheduleService;
import nl.tudelft.sem.template.schedule.external.ResourcesInterface;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockResourcesInterface")
@Configuration
public class MockResourcesInterfaceProfile {

    @Bean
    @Primary
    public ResourcesInterface getResourcesInterface() {
        return Mockito.mock(ResourcesInterface.class);
    }
}
