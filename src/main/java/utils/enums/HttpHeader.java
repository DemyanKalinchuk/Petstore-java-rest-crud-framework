package utils.enums;

import lombok.Getter;

@Getter
public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    AUTHORIZATION("Authorization"),
    ACCEPT_LANGUAGE("Accept-Language");

    private final String key;
    HttpHeader(String key){ this.key = key; }
}
