package fi.jereholopainen.tmnf_exchange_clone.exception;

public class MissingInputException extends BadRequestException {

    public MissingInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingInputException(String message) {
        super(message);
    }
}
