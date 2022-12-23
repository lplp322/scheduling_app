package nl.tudelft.sem.common.models.request.resources;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AvailableResourcesRequestModel {
    String faculty;
    LocalDate date;
}
