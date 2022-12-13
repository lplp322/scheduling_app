package nl.tudelft.sem.template.example.profiles;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.feigninterfaces.AuthenticationInterface;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockAuthMicrosrevice")
@Configuration
public class MockAuthenticationInterface {

    /**
     * Mocks the AuthenticationInterface.
     *
     * @return A mocked AuthenticationInterface.
     */
    @Bean
    @Primary
    public AuthenticationInterface getMockAuthenticationInterface() {
        System.out.println("Here2");
        return Mockito.mock(AuthenticationInterface.class);
    }
}
