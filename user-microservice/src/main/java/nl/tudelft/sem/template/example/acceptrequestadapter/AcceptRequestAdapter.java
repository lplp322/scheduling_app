package nl.tudelft.sem.template.example.acceptrequestadapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.tudelft.sem.template.example.feigninterfaces.WaitingListInterface;
import org.springframework.http.ResponseEntity;

/**
 * Main adapter class that implements AcceptRequest interface and has WaitingListInterface as parameter.
 */
public class AcceptRequestAdapter implements AcceptRequest {

    private transient WaitingListInterface waitingListInterface;

    public AcceptRequestAdapter(WaitingListInterface waitingListInterface) {
        this.waitingListInterface = waitingListInterface;
    }

    /**
     * Transforms AcceptRequestDataModel to ObjectNode data and calls WaitingListInterface.
     *
     * @param data - specific data model received from User side
     * @return response from WaitingListInterface
     */
    @Override
    public ResponseEntity acceptRequest(AcceptRequestDataModel data) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.valueToTree(data);
        return waitingListInterface.acceptRequest(objectNode);
    }
}
