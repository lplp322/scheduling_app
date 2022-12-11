package nl.tudelft.sem.template.example.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
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
public class RequestServiceTest1 {

    @Autowired
    private RequestService requestService;


    @Test
    @Transactional
    public void testSaveRequest() {
        // Create an object to save to the database
        RequestData obj = new RequestData();
        obj.setUser("Ivans");
        obj.setFaculty("CSE");
        obj.setCpu(1);
        obj.setGpu(2);
        obj.setMemory(3);
        obj.setDescription("Here");

        // Save the object using the service
        Long id = requestService.saveRequest(obj);

        // Verify that the object was saved by fetching it from the database
        Optional<UserRequest> savedObj = requestService.findById(id);
        assertTrue(savedObj.isPresent());
        UserRequest returnedObj = savedObj.get();
        assertEquals(obj.getUser(), returnedObj.getUser());
        assertEquals(obj.getFaculty(), returnedObj.getFaculty());
        assertEquals(obj.getCpu(), returnedObj.getCpu());
        assertEquals(obj.getGpu(), returnedObj.getGpu());
        assertEquals(obj.getMemory(), returnedObj.getMemory());
        assertEquals(obj.getDescription(), returnedObj.getDescription());
    }


}
