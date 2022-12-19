package nl.tudelft.sem.template.example.requestmodelget;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;

public class RequestModelFromJsonStrategy implements RequestModelCreatorStrategy {
    @Override
    public RequestModel createRequestModel(HttpServletRequest httpRequest) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModel request = objectMapper.readValue(httpRequest.getReader(), RequestModel.class);
        return request;
    }
}
