package config;

import utils.enums.SystemVar;

import java.io.IOException;
import java.util.Properties;

public final class Config {
    private static final Properties properties = new Properties();

    static {
        try (var inputStream = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream != null) properties.load(inputStream);
        } catch (IOException ignored) {}
    }

    private static String envOrProp(SystemVar systemVar){
        String env = System.getenv(systemVar.envKey);
        if (env != null && !env.isBlank()) return env;
        return properties.getProperty(systemVar.propKey, systemVar.defaultValue);
    }

    public static String baseApiUrl()      { return envOrProp(SystemVar.BASE_URL); }
    public static String baseFilesApiUrl() { return envOrProp(SystemVar.FILES_BASE_URL); }
    public static boolean consoleLog()     { return Boolean.parseBoolean(envOrProp(SystemVar.API_CONSOLE_LOG)); }
    public static int retryMax()           { return Integer.parseInt(envOrProp(SystemVar.API_RETRY_MAX)); }
    public static String acceptLang()      { return envOrProp(SystemVar.ACCEPT_LANG); }
    public static String bearer()          { return envOrProp(SystemVar.API_BEARER); }

    private Config() {}
}
