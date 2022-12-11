package nl.tudelft.sem.template.example.requests;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
    private transient RequestRepository repository;

    @Autowired
    public RequestService(RequestRepository repository) {
        this.repository = repository;
    }

    /**
     * Used to save request in nl.tudelft.sem.template.example.database.
     *
     * @param data - RequestData received from user
     * @return id of this request
     */
    public Long saveRequest(RequestData data) {
        UserRequest newRequest = new UserRequest(data.getUser(), data.getDescription(),
            data.getFaculty(), data.getCpu(), data.getGpu(),
            data.getMemory(), new Date(), "Pending");
        repository.save(newRequest);
        return newRequest.getId();
    }

    public Optional<UserRequest> findById(Long id) {
        return repository.findById(id);
    }
    /**
     * Used to get all requests of user with specific NetID.
     *
     * @param netId - netID of user
     * @return list with all requests
     */

    public List<UserRequest> getAllRequestsByNetId(String netId) {
        return repository.findByUser(netId);
    }
}
