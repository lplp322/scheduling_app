package nl.tudelft.sem.template.authentication.domain.user;

import java.util.ArrayList;
import java.util.Collection;
import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a list of roles in our domain.
 */
@EqualsAndHashCode
public class Roles {
    private final transient Collection<String> roleList;

    public Roles(Collection<String> roleList) {
        // Validate input
        this.roleList = roleList;
    }

    public Roles(String role) {
        this.roleList = new ArrayList<String>();
        this.roleList.add(role);
    }

    @Override
    public String toString() {
        return roleList.toString();
    }

    public Collection<String> getList() {
        return roleList;
    }
}
