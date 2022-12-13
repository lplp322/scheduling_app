package nl.tudelft.sem.common.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Collection;
import java.util.List;

public class JwtTokenUtils {

    /** Checks whether a token has all the roles passed to it or not.
     *
     * @param token token to check
     * @param roles collection of roles to be checked
     * @return true if every element in roles is present in token's roles
     */
    public static boolean checkTokenRoles(String token, Collection<String> roles) {
        Claims claims = Jwts.parser()
                .setSigningKey("exampleSecret")
                .parseClaimsJws(token)
                .getBody();

        List<String> tokenRoles = claims.get("roles", List.class);

        for (String i : roles) {
            if (!tokenRoles.contains(i)) {
                return false;
            }
        }
        return true;
    }


    /** Checks whether a token has all the roles passed to it or not.
     *
     * @param token token to check
     * @param roles collection of roles to be checked
     * @param secret secret encryption/decryption key
     * @return true if every element in roles is present in token's roles
     */
    public static boolean checkTokenRoles(String token, Collection<String> roles, String secret) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        List<String> tokenRoles = claims.get("roles", List.class);

        for (String i : roles) {
            if (!tokenRoles.contains(i)) {
                return false;
            }
        }
        return true;
    }
}
