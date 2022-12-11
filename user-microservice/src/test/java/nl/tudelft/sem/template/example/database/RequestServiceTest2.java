package nl.tudelft.sem.template.example.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import nl.tudelft.sem.template.example.requests.RequestData;
import nl.tudelft.sem.template.example.requests.RequestService;
import nl.tudelft.sem.template.example.requests.UserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest

public class RequestServiceTest2 {

    @Autowired
    private RequestService requestService;

    @Test
    @Transactional
    public void testGetAllRequestsByNetId() {
        RequestData obj = new RequestData();
        obj.setUser("Ivans2");
        RequestData obj2 = new RequestData();
        obj2.setUser("Ivans2");
        RequestData obj3 = new RequestData();
        obj3.setUser("Ivans3");
        requestService.saveRequest(obj);
        requestService.saveRequest(obj2);
        requestService.saveRequest(obj3);
        List<UserRequest> result = requestService.getAllRequestsByNetId("Ivans2");
        assertEquals(2, result.size());
        assertEquals(result.get(0).getUser(), "Ivans2");
    }
}
