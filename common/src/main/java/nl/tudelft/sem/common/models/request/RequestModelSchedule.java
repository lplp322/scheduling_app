package nl.tudelft.sem.common.models.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestModelSchedule {
    private long id; //Used to synchronise the IDs of requests across the microservices.
    private String name;
    private String description;
    private String faculty;
    private ResourcesModel resources;
    private LocalDate plannedDate;
}
