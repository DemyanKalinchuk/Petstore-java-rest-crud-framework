package utils.enums;

import lombok.Getter;

@Getter
public enum QueryParamKey {
    USERNAME("username"),
    PASSWORD("password");

    private final String key;

    QueryParamKey(String key) {
        this.key = key;
    }
}
