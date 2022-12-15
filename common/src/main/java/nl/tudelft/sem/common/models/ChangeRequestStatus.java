package nl.tudelft.sem.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.common.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRequestStatus {
    Long id;
    RequestStatus newStatus;
}
