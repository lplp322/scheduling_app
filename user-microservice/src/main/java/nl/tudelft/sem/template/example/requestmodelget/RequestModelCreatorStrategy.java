package nl.tudelft.sem.template.example.requestmodelget;

import javax.servlet.http.HttpServletRequest;

import nl.tudelft.sem.common.models.request.RequestModelWaitingList;

public interface RequestModelCreatorStrategy {
    RequestModelWaitingList createRequestModel(HttpServletRequest httpRequest) throws Exception;
}
