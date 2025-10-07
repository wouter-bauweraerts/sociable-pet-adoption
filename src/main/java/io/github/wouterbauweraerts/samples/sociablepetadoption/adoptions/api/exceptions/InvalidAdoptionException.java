package io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class InvalidAdoptionException extends IllegalArgumentException {
    public InvalidAdoptionException(String s) {
        super(s);
    }
}
