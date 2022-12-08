package nl.tudelft.sem.template.authentication.models;

import lombok.Data;
import nl.tudelft.sem.template.authentication.domain.user.Role;

/**
 * Model representing a registration request.
 */
@Data
public class RegistrationRequestModel {
    private String netId;
    private String password;
    private String role;
}