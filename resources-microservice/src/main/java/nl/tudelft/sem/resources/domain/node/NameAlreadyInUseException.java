package nl.tudelft.sem.resources.domain.node;

public class NameAlreadyInUseException extends Exception {
    public NameAlreadyInUseException(String token) {
        super(token);
    }
}
