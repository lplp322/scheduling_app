package nl.tudelft.sem.template.example.feigninterfaces;

import lombok.Data;

import java.util.Collection;

/**
 * Model representing a registration request.
 */
@Data
public class RegistrationRequestModel {
    private String netId;
    private String password;
    private Collection<String> roles;
}