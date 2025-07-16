package org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String s) {
        super(s);
    }
}
