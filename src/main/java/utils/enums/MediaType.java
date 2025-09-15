package utils.enums;

import lombok.Getter;

@Getter
public enum MediaType {
    APPLICATION_JSON("application/json"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    TEXT_HTML("text/html");

    private final String value;
    MediaType(String value){ this.value = value; }
}
