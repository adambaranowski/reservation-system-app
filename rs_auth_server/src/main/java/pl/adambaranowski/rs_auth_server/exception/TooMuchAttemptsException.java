package pl.adambaranowski.rs_auth_server.exception;

public class TooMuchAttemptsException extends RuntimeException {
    public TooMuchAttemptsException(String message) {
        super(message);
    }
}
