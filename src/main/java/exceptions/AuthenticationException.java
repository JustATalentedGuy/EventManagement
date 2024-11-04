package exceptions;

// Custom exception for authentication failures
public class AuthenticationException extends AlertException {
    
    public AuthenticationException(String message) {
        super(message);
    }
}
