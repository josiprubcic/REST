package hr.fer.rest_api.dto.response;

import java.time.Instant;

public class ErrorResponse {

    private final Instant timestamp;
    private final String code;
    private final String message;
    private final String path;

    public ErrorResponse(String code, String message, String path) {
        this.timestamp = Instant.now();
        this.code = code;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}