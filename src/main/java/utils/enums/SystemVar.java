package utils.enums;

public enum SystemVar {
    BASE_URL("BASE_URL", "api.base.url", "https://petstore.swagger.io/v2"),
    FILES_BASE_URL("FILES_BASE_URL", "files.base.url", "https://petstore.swagger.io/v2"),
    API_CONSOLE_LOG("API_CONSOLE_LOG", "api.console.log", "true"),
    API_RETRY_MAX("API_RETRY_MAX", "api.retry.max", "2"),
    ACCEPT_LANG("ACCEPT_LANG", "accept.lang", "en-US"),
    API_BEARER("API_BEARER", "api.bearer", "special-key");

    public final String envKey;
    public final String propKey;
    public final String defaultValue;

    SystemVar(String envKey, String propKey, String defaultValue) {
        this.envKey = envKey;
        this.propKey = propKey;
        this.defaultValue = defaultValue;
    }
}
