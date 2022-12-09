package nl.tudelft.sem.template.example.requests;

import java.util.Date;
import nl.tudelft.sem.template.example.domain.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
    private transient RequestRepository repository;

    @Autowired
    public RequestService(RequestRepository repository) {
        this.repository = repository;
    }

    /**
     * Used to save request in database.
     *
     * @param data - RequestData received from user
     * @return id of this request
     */
    public Long saveRequest(RequestData data) {
        UserRequest newRequest = new UserRequest(data.getUser(), data.getDescription(),
            data.getCpu(), data.getGpu(), data.getMemory(), new Date(), "Pending");
        repository.save(newRequest);
        return newRequest.getId();
    }
}
