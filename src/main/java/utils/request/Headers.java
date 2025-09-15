package utils.request;

public class Headers {
    private final String headerKey;
    private final String headerValue;
    public Headers(String headerKey, String headerValue){ this.headerKey = headerKey; this.headerValue = headerValue; }
    public int getSize(){ return headerKey == null ? 0 : 1; }
    public String[] getHeader(){ return new String[]{ headerKey, headerValue }; }
    public static Headers of(String key, String value){ return new Headers(key, value); }
}
