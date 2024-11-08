package fi.jereholopainen.tmnf_exchange_clone.exception;

public class MissingTmnfLoginException extends RuntimeException {

    public MissingTmnfLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingTmnfLoginException(String message) {
        super(message);
    }

}
