package nl.tudelft.sem.template.authentication.domain.user;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a User Role (Sysadmin, Faculty Admin or regular employee) in our domain.
 */
public enum Role {
    SYSADMIN, FACULTYADMIN, EMPLOYEE;
}
