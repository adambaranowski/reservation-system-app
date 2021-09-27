package pl.adambaranowski.rsbackend.exception;

public class TooMuchAttemptsException extends RuntimeException{
    public TooMuchAttemptsException(String message) {
        super(message);
    }
}
