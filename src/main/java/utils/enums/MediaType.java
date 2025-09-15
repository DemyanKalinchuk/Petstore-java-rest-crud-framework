package utils.enums;

public enum MediaType {
    APPLICATION_JSON("application/json"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    TEXT_HTML("text/html");

    public final String value;
    MediaType(String value){ this.value = value; }
}
