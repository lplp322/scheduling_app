package nl.tudelft.sem.template.example.requestmodelget;

import java.io.BufferedReader;
import java.io.IOException;
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
    private static final int arrayLength = 7;

    @Override
    public RequestModel createRequestModel(HttpServletRequest httpRequest) throws Exception {
        BufferedReader reader = httpRequest.getReader();
        try {
            String requestLine = reader.lines().collect(Collectors.joining());
            reader.close();
            String[] parameterArray = requestLine.split(",");
            if (parameterArray.length != arrayLength) {
                throw new Exception("Not correct PlainText model, check RequestModelFromPlainText class comments");
            }
            return new RequestModel(parameterArray[0], parameterArray[1], parameterArray[2],
                new ResourcesModel(Integer.parseInt(parameterArray[3]),
                    Integer.parseInt(parameterArray[4]), Integer.parseInt(parameterArray[5])),
                LocalDate.parse(parameterArray[6]));
        } catch (IOException e) {
            throw new IOException("Input is wrong");
        } finally {
            reader.close();
        }
    }
}
