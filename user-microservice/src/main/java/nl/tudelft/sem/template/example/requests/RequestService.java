package nl.tudelft.sem.template.example.requests;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.common.RequestStatus;
import nl.tudelft.sem.common.models.request.RequestModel;
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
     * Used to save request in nl.tudelft.sem.template.example.database.
     *
     * @param id - id of the request
     * @param data - RequestData received from user
     * @return id of this request
     */
    public Long saveRequest(RequestModel data, Long id) {
        UserRequest newRequest = new UserRequest(id, data.getName(), data.getDescription(),
            data.getFaculty(), RequestStatus.PENDING);
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
