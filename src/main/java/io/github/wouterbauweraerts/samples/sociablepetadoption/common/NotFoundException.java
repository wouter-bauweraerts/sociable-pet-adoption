package io.github.wouterbauweraerts.samples.sociablepetadoption.common;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException withTypeAndId(String type, Integer id) {
        return new NotFoundException("No %s with id %d found.".formatted(type, id));
    }
}
