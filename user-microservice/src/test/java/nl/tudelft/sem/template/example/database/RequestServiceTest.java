package nl.tudelft.sem.template.example.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.common.RequestStatus;
import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;
import nl.tudelft.sem.template.example.requests.RequestData;
import nl.tudelft.sem.template.example.requests.RequestService;
import nl.tudelft.sem.template.example.requests.UserRequest;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RequestServiceTest {

    @Autowired
    private RequestService requestService;


    @Test
    @Transactional
    public void testSaveRequest() {
        // Create an object to save to the database
        RequestModel obj = new RequestModel();
        obj.setName("Ivans");
        obj.setFaculty("CSE");
        obj.setDescription("Here");

        // Save the object using the service
        Long id = requestService.saveRequest(obj, 1L);

        // Verify that the object was saved by fetching it from the database
        Optional<UserRequest> savedObj = requestService.findById(id);
        assertTrue(savedObj.isPresent());
        UserRequest returnedObj = savedObj.get();
        assertEquals(obj.getName(), returnedObj.getUser());
        assertEquals(obj.getFaculty(), returnedObj.getFaculty());
        assertEquals(obj.getDescription(), returnedObj.getDescription());
        assertEquals(RequestStatus.PENDING, returnedObj.getStatus());
    }

    @Test
    @Transactional
    public void testGetAllRequestsByNetId() {
        RequestModel obj = new RequestModel();
        obj.setName("Ivans2");
        RequestModel obj2 = new RequestModel();
        obj2.setName("Ivans2");
        RequestModel obj3 = new RequestModel();
        obj3.setName("Ivans3");
        requestService.saveRequest(obj, 1L);
        requestService.saveRequest(obj2, 2L);
        requestService.saveRequest(obj3, 3L);
        List<UserRequest> result = requestService.getAllRequestsByNetId("Ivans2");
        assertEquals(2, result.size());
        assertEquals(result.get(0).getUser(), "Ivans2");
    }
}