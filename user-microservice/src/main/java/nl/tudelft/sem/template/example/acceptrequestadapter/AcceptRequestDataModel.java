package nl.tudelft.sem.template.example.acceptrequestadapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data model that is used by FacultyAdminController and AcceptRequest interface
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptRequestDataModel {
    private Long id;
    private String plannedDate;
}
