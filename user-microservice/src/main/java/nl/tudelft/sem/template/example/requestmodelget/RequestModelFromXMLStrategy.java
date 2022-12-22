package nl.tudelft.sem.template.example.requestmodelget;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.beans.XMLDecoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.request.ResourcesModel;

/**
 * Creating RequestModel from just PlainText body of the request.
 * Request pattern should be:
 * "${name},${description},${faculty},${resources.cpu},${resources.gpu},${resources.mem},${deadline}".
 */
public class RequestModelFromXMLStrategy implements RequestModelCreatorStrategy {
    private static final int arrayLength = 7;

    @Override
    public RequestModelWaitingList createRequestModel(HttpServletRequest httpRequest) throws Exception {
//        BufferedReader reader = httpRequest.getReader();
//        try {
//            String requestLine = reader.lines().collect(Collectors.joining());
//            reader.close();
//            String[] parameterArray = requestLine.split(",");
//            if (parameterArray.length != arrayLength) {
//                throw new Exception("Not correct PlainText model, check RequestModelFromPlainText class comments");
//            }
//            return new RequestModelWaitingList(parameterArray[0], parameterArray[1], parameterArray[2],
//                new ResourcesModel(Integer.parseInt(parameterArray[3]),
//                    Integer.parseInt(parameterArray[4]), Integer.parseInt(parameterArray[5])),
//                LocalDate.parse(parameterArray[6]));
//        } catch (IOException e) {
//            throw new IOException("Input is wrong");
//        } finally {
//            reader.close();
//        }
        JAXBContext jaxbContext;
    }
}
