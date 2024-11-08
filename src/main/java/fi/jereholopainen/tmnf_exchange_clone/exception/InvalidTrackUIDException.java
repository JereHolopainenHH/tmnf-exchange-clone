package fi.jereholopainen.tmnf_exchange_clone.exception;

public class InvalidTrackUIDException extends RuntimeException {
    // Constructor to accept a custom message and an exception cause
    public InvalidTrackUIDException(String message, Throwable cause) {
        super(message, cause); // Pass both the message and the cause to the superclass (RuntimeException)
    }

    // Optional: Constructor to accept only a message
    public InvalidTrackUIDException(String message) {
        super(message); // Pass the message to the superclass
    }
}
