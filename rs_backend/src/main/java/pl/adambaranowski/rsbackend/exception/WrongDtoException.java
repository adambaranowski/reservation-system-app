package pl.adambaranowski.rsbackend.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class WrongDtoException extends RuntimeException {
    private final List<String> errors;

    public WrongDtoException(List<String> errors) {
        this.errors = errors;
    }
}
