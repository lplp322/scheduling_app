package nl.tudelft.sem.template.example.getrequestadapter;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptRequestDataModel {
    private Long id;
    private String plannedDate;
}
