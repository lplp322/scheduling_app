package nl.tudelft.sem.template.schedule.database;

import nl.tudelft.sem.common.RequestStatus;
import nl.tudelft.sem.common.models.request.RequestModel;
import nl.tudelft.sem.template.schedule.domain.request.RequestRepository;
import nl.tudelft.sem.template.schedule.domain.request.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ScheduleServiceTest {

    @Autowired
    private ScheduleService scheduleService;

    private RequestRepository mockRepo;

    @BeforeEach
    public void setUp() {
        this.mockRepo = Mockito.mock(RequestRepository.class);
    }

    /*@Test
    public void testScheduleRequest() {
        // Create a request to schedule
        RequestModel request = new RequestModel();
        request.setName("Bink");
        request.setFaculty("CSE");
        request.setDescription("Test");

        // Save the object using the service
        scheduleService.scheduleRequest(request);

        // Verify that the object was saved by fetching it from the database
        Optional<UserRequest> savedObj = requestService.findById(id);
        assertTrue(savedObj.isPresent());
        UserRequest returnedObj = savedObj.get();
        assertEquals(obj.getName(), returnedObj.getUser());
        assertEquals(obj.getFaculty(), returnedObj.getFaculty());
        assertEquals(obj.getDescription(), returnedObj.getDescription());
        assertEquals(RequestStatus.PENDING, returnedObj.getStatus());
    }*/
}
