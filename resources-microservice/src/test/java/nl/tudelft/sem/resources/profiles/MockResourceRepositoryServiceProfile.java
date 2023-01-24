package nl.tudelft.sem.resources.profiles;

import nl.tudelft.sem.resources.domain.resources.ResourceRepositoryService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockResourceRepositoryService")
@Configuration
public class MockResourceRepositoryServiceProfile {

    @Bean
    @Primary
    public ResourceRepositoryService getResourceRepositoryService() {
        return Mockito.mock(ResourceRepositoryService.class);
    }
}
