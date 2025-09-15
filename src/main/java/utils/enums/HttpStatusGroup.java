package utils.enums;

import java.util.Set;

public final class HttpStatusGroup {
    private HttpStatusGroup(){}
    public static final Set<Integer> SUCCESS_CODES = Set.of(
        HttpStatusCode.OK.code, HttpStatusCode.CREATED.code, HttpStatusCode.ACCEPTED.code,
        HttpStatusCode.NO_CONTENT.code, HttpStatusCode.RESET_CONTENT.code
    );
    public static final Set<Integer> RETRYABLE_CODES = Set.of(
        HttpStatusCode.CONFLICT.code, HttpStatusCode.GONE.code, HttpStatusCode.TOO_MANY_REQUESTS.code,
        HttpStatusCode.INTERNAL_SERVER_ERROR.code, HttpStatusCode.BAD_GATEWAY.code
    );
}
