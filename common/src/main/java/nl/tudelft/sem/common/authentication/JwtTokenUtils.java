package nl.tudelft.sem.common.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenUtils {

    /** Checks whether a token has all the roles passed to it or not.
     *
     * @param token token to check
     * @param roles collection of roles to be checked
     * @return true if every element in roles is present in token's roles
     */
    public static boolean checkTokenRoles(String token, Collection<String> roles) {
        return checkTokenRoles(token, roles, "exampleSecret");
    }

    /** Checks whether a token has all the roles passed to it or not.
     *
     * @param token token to check
     * @param roles collection of roles to be checked
     * @return true if every element in roles is present in token's roles
     */
    public static boolean checkTokenRoles(String token, String role) {
        List<String> roles = new ArrayList<>();
        roles.add(role);
        return checkTokenRoles(token, roles, "exampleSecret");
    }

    /** Checks whether a token has all the roles passed to it or not.
     *
     * @param token token to check
     * @param roles collection of roles to be checked
     * @param secret secret encryption/decryption key
     * @return true if every element in roles is present in token's roles
     */
    public static boolean checkTokenRoles(String token, Collection<String> roles, String secret) {
        if(roles.isEmpty())
            return true;
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        Collection<String> tokenRoles =
                Arrays.stream(claims.get("roles").toString().replaceAll("[\\[\\]{}]", "").split(","))
                        .map((String s) -> {return s.replace("authority=", "");})
                        .collect(Collectors.toList());
        for (String i : roles) {
            if (tokenRoles.contains(i)) {
                return true;
            }
        }
        return false;
    }
}
