package nl.tudelft.sem.template.example.requestmodelget;

import javax.servlet.http.HttpServletRequest;

import nl.tudelft.sem.common.models.request.RequestModelWaitingList;

/**
 * Interface that is used to create different strategies 
 */
public interface RequestModelCreatorStrategy {
    RequestModelWaitingList createRequestModel(HttpServletRequest httpRequest) throws Exception;
}
