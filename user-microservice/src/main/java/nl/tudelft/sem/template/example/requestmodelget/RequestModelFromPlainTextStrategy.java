package nl.tudelft.sem.template.example.requestmodelget;

import java.io.BufferedReader;
import java.time.LocalDate;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;
import nl.tudelft.sem.common.models.request.waitinglist.ResourcesModel;

/**
 * Creating RequestModel from just PlainText body of the request.
 * Request pattern should be:
 * "${name},${description},${faculty},${resources.cpu},${resources.gpu},${resources.mem},${deadline}".
 */
public class RequestModelFromPlainTextStrategy implements RequestModelCreatorStrategy {
    @Override
    public RequestModel createRequestModel(HttpServletRequest httpRequest) throws Exception {
        BufferedReader reader = httpRequest.getReader();
        RequestModel request = new RequestModel();
        String requestLine = reader.lines().collect(Collectors.joining());
        String[] parameterArray = requestLine.split(",");
        if (parameterArray.length != 7) {
            throw new Exception("Not correct PlainText model, check RequestModelFromPlainText class comments");
        }
        request.setName(parameterArray[0]);
        request.setDescription(parameterArray[1]);
        request.setFaculty(parameterArray[2]);
        request.setResources(new ResourcesModel(Integer.parseInt(parameterArray[3]),
            Integer.parseInt(parameterArray[4]), Integer.parseInt(parameterArray[5])));
        request.setDeadline(LocalDate.parse(parameterArray[6]));
        return request;
    }
}
