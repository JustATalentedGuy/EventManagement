package exceptions;

// Custom exception for authorization failures
public class IncompleteFieldException extends AlertException {
    
    public IncompleteFieldException(String message) {
        super(message);
    }
}