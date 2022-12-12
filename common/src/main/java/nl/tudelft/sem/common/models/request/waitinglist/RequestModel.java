package nl.tudelft.sem.common.models.request.waitinglist;

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
    private LocalDate deadline;
}
