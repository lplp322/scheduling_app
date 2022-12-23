package nl.tudelft.sem.template.schedule.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Authentication Manager.
 */
@Component
public class AuthManager {
    /**
     * Interfaces with spring security to get the name of the user in the current context.
     *
     * @return The name of the user.
     */
    public String getNetId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Get the roles of the user making the request to check if the user is authorized.
     *
     * @return The roles that the user has.
     */
    public Collection<? extends GrantedAuthority> getRoles() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }
}
