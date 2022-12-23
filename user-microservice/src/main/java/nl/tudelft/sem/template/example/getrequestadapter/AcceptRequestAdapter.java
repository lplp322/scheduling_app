package nl.tudelft.sem.template.example.getrequestadapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.time.LocalDate;
import nl.tudelft.sem.template.example.feigninterfaces.WaitingListInterface;
import org.springframework.http.ResponseEntity;

public class AcceptRequestAdapter implements AcceptRequest {

    private transient WaitingListInterface waitingListInterface;

    public AcceptRequestAdapter(WaitingListInterface waitingListInterface) {
        this.waitingListInterface = waitingListInterface;
    }

    @Override
    public ResponseEntity acceptRequest(AcceptRequestDataModel data) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.valueToTree(data);
        return waitingListInterface.acceptRequest(objectNode);
    }
}
