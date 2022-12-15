package nl.tudelft.sem.template.authentication.domain.user;

import java.util.ArrayList;
import java.util.Collection;
import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a role in our domain.
 */
@EqualsAndHashCode
public class Roles {
    private final transient Collection<String> role;

    public Roles(Collection<String> role) {
        // Validate input
        this.role = role;
    }

    public Roles(String role) {
        this.role = new ArrayList<String>();
        this.role.add(role);
    }

    @Override
    public String toString() {
        return role.toString();
    }

    public Collection<String> getList() {
        return role;
    }
}
