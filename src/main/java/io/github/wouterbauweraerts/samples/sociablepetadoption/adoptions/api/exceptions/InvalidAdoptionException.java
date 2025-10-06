package io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.exceptions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(BAD_REQUEST)
public class InvalidAdoptionException extends IllegalArgumentException {
    public InvalidAdoptionException(String s) {
        super(s);
    }
}
