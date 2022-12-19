package nl.tudelft.sem.template.example.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.template.example.domain.RequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RequestRepository userRepository;

    @Test
    @Transactional
    public void testSaveToDatabase() {
        // Save a user to the database
        UserRequest user = new UserRequest();
        user.setId(1L);
        user.setUser("John");
        entityManager.persist(user);

        // Retrieve the user from the database using the JpaRepository
        Optional<UserRequest> result = userRepository.findById(1L);
        assertEquals(result.get().getUser(), "John");
    }

    @Test
    @Transactional
    public void testFindByUser() {
        UserRequest user = new UserRequest();
        user.setId(1L);
        user.setUser("Ivans");
        user.setStatus(RequestStatus.PENDING);
        entityManager.persist(user);
        UserRequest user2 = new UserRequest();
        user2.setId(2L);
        user2.setUser("Ivans");
        user2.setStatus(RequestStatus.PENDING);
        entityManager.persist(user2);
        List<UserRequest> result = userRepository.findByUser("Ivans");
        assertEquals(2, result.size());
        assertEquals(result.get(0).getUser(), "Ivans");
        assertEquals(result.get(0).getStatus(), RequestStatus.PENDING);
    }
}
