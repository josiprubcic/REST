package hr.fer.rest_api.exception;

public class RequestValidationException extends RuntimeException {
    public RequestValidationException(String s) {
        super(s);
    }
}
