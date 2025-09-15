package utils.enums;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    AUTHORIZATION("Authorization"),
    ACCEPT_LANGUAGE("Accept-Language");

    public final String key;
    HttpHeader(String key){ this.key = key; }
}
