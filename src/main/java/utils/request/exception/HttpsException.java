package utils.request.exception;

public class HttpsException extends RuntimeException {
    public HttpsException(String message){ super(message); }
    public HttpsException(String message, Throwable cause){ super(message, cause); }
}
