package nl.tudelft.sem.common.models.request.resources;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.common.models.request.ResourcesModel;

import java.net.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostNodeRequestModel {
    private String name;
    private URL url;
    private String token;
    private ResourcesModel resources;
    private String faculty;

}
