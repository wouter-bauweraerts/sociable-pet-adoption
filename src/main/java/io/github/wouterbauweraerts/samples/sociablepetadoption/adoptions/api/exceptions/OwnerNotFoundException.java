package io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.exceptions;

public class OwnerNotFoundException extends InvalidAdoptionException {
    public OwnerNotFoundException(String s) {
        super(s);
    }

    public static OwnerNotFoundException withId(Integer id) {
        return new OwnerNotFoundException("Owner %d is not available for adoption.".formatted(id));
    }
}
