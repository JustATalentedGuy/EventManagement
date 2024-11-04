package exceptions;

// Custom exception for scheduling conflicts
public class ConflictException extends AlertException {
    
    public ConflictException(String message) {
        super(message);
    }
}
