package nl.tudelft.sem.waitinglist.domain;

import java.util.NoSuchElementException;
import nl.tudelft.sem.waitinglist.database.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SingleTableWaitingList implements WaitingList {

    private final transient RequestRepository requestRepo;

    @Autowired
    public SingleTableWaitingList(RequestRepository requestRepo) {
        this.requestRepo = requestRepo;
    }

    @Override
    public Long addRequest(Request request) {
        // Check that request does not have ID yet
        if (request.getId() != null) {
            throw new IllegalArgumentException("To be added request cannot have an ID");
        }

        Request savedRequest = requestRepo.save(request);
        return savedRequest.getId();
    }

    @Override
    public void rejectRequest(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!requestRepo.existsById(id)) {
            throw new NoSuchElementException("A request with such id does not exist");
        }

        requestRepo.deleteById(id);
    }
}
