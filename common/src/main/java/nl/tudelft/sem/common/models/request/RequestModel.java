package nl.tudelft.sem.common.models.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestModel {
    private String name;
    private String description;
    private String faculty;
    private ResourcesModel resources;
    private LocalDate date; //Deadline for Waiting List and planned date for Schedule.
    private String netId;
}
