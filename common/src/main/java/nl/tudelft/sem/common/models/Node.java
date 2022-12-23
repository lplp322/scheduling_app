package nl.tudelft.sem.common.models;

import java.net.URL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.common.models.request.ResourcesModel;

/**
 * DataTransferObject to get new Node info from user to User microservice and then send it to Resources microservice.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node {
    private String name;
    private URL url;
    private String token;
    private ResourcesModel resources;
    private String faculty;
}
