package utils.enums;

import lombok.Getter;

@Getter
public enum HttpStatusCode {
    OK(200), CREATED(201), ACCEPTED(202), NO_CONTENT(204), RESET_CONTENT(205),
    NOT_MODIFIED(304),
    BAD_REQUEST(400), UNAUTHORIZED(401), FORBIDDEN(403), NOT_FOUND(404), CONFLICT(409), GONE(410), TOO_MANY_REQUESTS(429),
    INTERNAL_SERVER_ERROR(500), BAD_GATEWAY(502), SERVICE_UNAVAILABLE(503), GATEWAY_TIMEOUT(504);


    private final int statusCode;

    HttpStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
