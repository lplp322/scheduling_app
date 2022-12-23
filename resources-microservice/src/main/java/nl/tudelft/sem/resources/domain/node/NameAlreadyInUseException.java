package nl.tudelft.sem.resources.domain.node;

public class NameAlreadyInUseException extends Exception {

    public static final long serialVersionUID = 4328743;

    public NameAlreadyInUseException(String token) {
        super(token);
    }
}
