package nl.tudelft.sem.common.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestModelWaitingListId {
    private long id;
    private String name;
    private String description;
    private String faculty;
    private ResourcesModel resources;
    private LocalDate deadline;
}
