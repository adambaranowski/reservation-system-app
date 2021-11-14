package pl.adambaranowski.rsbackend.exception;

public class ExternalServiceCommunicationException extends RuntimeException{
    public ExternalServiceCommunicationException(String message) {
        super(message);
    }
}
