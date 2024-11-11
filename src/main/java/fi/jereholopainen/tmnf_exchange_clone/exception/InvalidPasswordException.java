package fi.jereholopainen.tmnf_exchange_clone.exception;

public class InvalidPasswordException extends BadRequestException{

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPasswordException(String message) {
        super(message);
    }
}
