package fi.jereholopainen.tmnf_exchange_clone.exception;

public class InvalidFileTypeException extends BadRequestException {
    public InvalidFileTypeException(String message) {
        super(message);
    }
}
