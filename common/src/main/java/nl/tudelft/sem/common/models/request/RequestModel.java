package nl.tudelft.sem.common.models.request;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestModel  {
    @JsonProperty("id")
    private Optional<Long> id; //Used to synchronise the IDs of requests across the microservices.
    private String name;
    private String description;
    private String faculty;
    private ResourcesModel resources;
    private LocalDate deadline; //Deadline for Waiting List and planned date for Schedule.
    private String netId;
}
