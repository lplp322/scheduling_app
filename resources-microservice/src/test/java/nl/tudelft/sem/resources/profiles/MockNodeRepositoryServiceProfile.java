package nl.tudelft.sem.resources.profiles;

import nl.tudelft.sem.resources.domain.node.NodeRepositoryService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockNodeRepositoryService")
@Configuration
public class MockNodeRepositoryServiceProfile {

    @Bean
    @Primary
    public NodeRepositoryService getNodeRepositoryService() {
        return Mockito.mock(NodeRepositoryService.class);
    }
}
