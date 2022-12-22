package nl.tudelft.sem.template.example.requestmodelget;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;

/**
 * Strategy to create objects from JSON string.
 * To deserialize objects ObjectMapper is used
 */
public class RequestModelFromJsonStrategy implements RequestModelCreatorStrategy {
    @Override
    public RequestModelWaitingList createRequestModel(HttpServletRequest httpRequest) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModelWaitingList request = objectMapper.readValue(httpRequest.getReader(), RequestModelWaitingList.class);
        return request;
    }
}
