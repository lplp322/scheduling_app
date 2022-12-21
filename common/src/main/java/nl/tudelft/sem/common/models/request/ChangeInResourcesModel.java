package nl.tudelft.sem.common.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeInResourcesModel {
    LocalDate date;
    ResourcesModel changedResources;
}
