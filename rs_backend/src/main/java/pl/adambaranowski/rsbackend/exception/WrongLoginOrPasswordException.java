package pl.adambaranowski.rsbackend.exception;

public class WrongLoginOrPasswordException extends RuntimeException {
    public WrongLoginOrPasswordException(String message) {
        super(message);
    }
}
