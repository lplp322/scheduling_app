package nl.tudelft.sem.template.example.requestmodelget;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;

public interface RequestModelCreatorStrategy {
    RequestModel createRequestModel(HttpServletRequest httpRequest) throws Exception;
}
