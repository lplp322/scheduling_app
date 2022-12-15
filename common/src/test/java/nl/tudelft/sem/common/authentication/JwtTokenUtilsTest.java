package nl.tudelft.sem.common.authentication;

import static org.assertj.core.api.Assertions.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

class JwtTokenUtilsTest {
    @Test
    void checkTokenRoles() {
        String username = "iciobanu";
        Collection<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("admin"));
        List<String> toCheck = new ArrayList<>();
        toCheck.add("admin");
        Map<String, Object> claims = new HashMap<>();
        String token = Jwts.builder().setClaims(claims).setSubject(username)
                .claim("roles", roles)
                .signWith(SignatureAlgorithm.HS512, "exampleSecret").compact();
        assertThat(JwtTokenUtils.checkTokenRoles(token, toCheck)).isTrue();
    }

    @Test
    void testCheckTokenRoles() {
        String username = "iciobanu";
        Collection<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("admin"));
        List<String> toCheck = new ArrayList<>();
        toCheck.add("admin");
        toCheck.add("user");
        Map<String, Object> claims = new HashMap<>();
        String token = Jwts.builder().setClaims(claims).setSubject(username)
                .claim("roles", roles)
                .signWith(SignatureAlgorithm.HS512, "exampleSecret").compact();
        assertThat(JwtTokenUtils.checkTokenRoles(token, toCheck)).isTrue();
    }

    @Test
    void testCheckTokenRoles2() {
        String username = "iciobanu";
        Collection<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("admin"));
        roles.add(new SimpleGrantedAuthority("user"));
        roles.add(new SimpleGrantedAuthority("sysadmin"));
        List<String> toCheck = new ArrayList<>();
        toCheck.add("admin");
        Map<String, Object> claims = new HashMap<>();
        String token = Jwts.builder().setClaims(claims).setSubject(username)
                .claim("roles", roles)
                .signWith(SignatureAlgorithm.HS512, "exampleSecret").compact();
        assertThat(JwtTokenUtils.checkTokenRoles(token, toCheck)).isTrue();
    }
}