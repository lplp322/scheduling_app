package nl.tudelft.sem.common.models;

/**
 * Enum to change status of the request in the User microservice, is used by User, WaitingList and directly FacultyAdmins
 * it is sent from WaitingList to User or directly from Faculty Admin to User microservice.
 */
public enum RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}
