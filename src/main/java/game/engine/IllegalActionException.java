package game.engine;

/**
 * Exception thrown when an action is invalid or cannot be performed.
 */
public class IllegalActionException extends Exception {
    
    public IllegalActionException(String message) {
        super(message);
    }
    
    public IllegalActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
