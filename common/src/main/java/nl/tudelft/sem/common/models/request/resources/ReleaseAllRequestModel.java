package nl.tudelft.sem.common.models.request.resources;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ReleaseAllRequestModel {
    LocalDate day;
}
