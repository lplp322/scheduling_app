package nl.tudelft.sem.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This DataTransferObject is sent from WaitingList to User or directly from Faculty Admin to User microservice.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRequestStatus {
    Long id;
    RequestStatus newStatus;
}
