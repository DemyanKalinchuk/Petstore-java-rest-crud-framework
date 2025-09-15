package utils.enums;

import java.util.Set;

public final class HttpStatusGroup {
    private HttpStatusGroup(){}
    public static final Set<Integer> SUCCESS_CODES = Set.of(
        HttpStatusCode.OK.getStatusCode(), HttpStatusCode.CREATED.getStatusCode(), HttpStatusCode.ACCEPTED.getStatusCode(),
        HttpStatusCode.NO_CONTENT.getStatusCode(), HttpStatusCode.RESET_CONTENT.getStatusCode()
    );
    public static final Set<Integer> RETRYABLE_CODES = Set.of(
        HttpStatusCode.CONFLICT.getStatusCode(), HttpStatusCode.GONE.getStatusCode(), HttpStatusCode.TOO_MANY_REQUESTS.getStatusCode(),
        HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), HttpStatusCode.BAD_GATEWAY.getStatusCode()
    );
}
