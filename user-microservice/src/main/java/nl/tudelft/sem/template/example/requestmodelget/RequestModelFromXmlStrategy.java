package nl.tudelft.sem.template.example.requestmodelget;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.servlet.http.HttpServletRequest;

import javax.xml.bind.JAXBContext;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;

/**
 * Creating RequestModel from just PlainText body of the request.
 * Request pattern should be:
 * "${name},${description},${faculty},${resources.cpu},${resources.gpu},${resources.mem},${deadline}".
 */
public class RequestModelFromXmlStrategy implements RequestModelCreatorStrategy {

    @Override
    public RequestModelWaitingList createRequestModel(HttpServletRequest httpRequest) throws Exception {
        System.out.println("here");
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        return xmlMapper.readValue(httpRequest.getReader(),
            RequestModelWaitingList.class);
    }
}
