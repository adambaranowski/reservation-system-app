package pl.adambaranowski.rs_auth_server.exception;

public class WrongLoginOrPasswordException extends RuntimeException {
    public WrongLoginOrPasswordException(String message) {
        super(message);
    }
}
