package nl.tudelft.sem.common.models.request.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.common.models.request.ResourcesModel;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseRequestModel {

    private ResourcesModel releasedResources;
    private String faculty;
    private LocalDate from;
    private LocalDate until;
}
