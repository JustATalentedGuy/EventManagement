package exceptions;

// Custom exception for authorization failures
public class AuthorizationException extends AlertException {
    
    public AuthorizationException(String message) {
        super(message);
    }
}