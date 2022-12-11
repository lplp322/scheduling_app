package nl.tudelft.sem.template.example.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.RequestRepository;
import nl.tudelft.sem.template.example.requests.UserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@DataJpaTest
public class RequestTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RequestRepository userRepository;

    @Test
    public void testSaveToDatabase() {
        // Save a user to the database
        UserRequest user = new UserRequest();
        user.setUser("John");
        entityManager.persist(user);

        // Retrieve the user from the database using the JpaRepository
        Optional<UserRequest> result = userRepository.findById(1L);
        assertEquals(result.get().getUser(), "John");
    }

    @Test
    public void testFindByUser() {
        UserRequest user = new UserRequest();
        user.setUser("Ivans");
        user.setCondition("test");
        entityManager.persist(user);
        UserRequest user2 = new UserRequest();
        user2.setUser("Ivans");
        user2.setCondition("test");
        entityManager.persist(user2);
        List<UserRequest> result = userRepository.findByUser("Ivans");
        assertEquals(2, result.size());
        assertEquals(result.get(0).getUser(), "Ivans");
    }
}
