package config;

import utils.enums.SystemVar;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public final class Config {
    private static final String BASE_FILE = "application.properties";
    private static final String ENV_KEY = "env";
    private static final String ENV_FILE_TEMPLATE = "application-%s.properties";

    private static final Properties PROPS = load();

    private Config() {}

    private static Properties load() {
        Properties merged = new Properties();

        // 1) Load base application.properties
        try (InputStream in = resource(BASE_FILE)) {
            if (in != null) {
                merged.load(in);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load " + BASE_FILE, ex);
        }

        // 2) Determine env from system prop or env var (set by Maven profile or CLI)
        String activeEnv = Optional.ofNullable(System.getProperty(ENV_KEY))
                .orElseGet(() -> System.getenv(ENV_KEY) == null ? null : System.getenv(ENV_KEY));
        if (activeEnv == null || activeEnv.isBlank()) {
            activeEnv = "dev"; // default
        }

        // 3) Overlay application-<env>.properties if present
        String envFile = String.format(ENV_FILE_TEMPLATE, activeEnv);
        try (InputStream in = resource(envFile)) {
            if (in != null) {
                Properties overlay = new Properties();
                overlay.load(in);
                merged.putAll(overlay);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load " + envFile, ex);
        }

        return merged;
    }

    private static InputStream resource(String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
    }

    // ---- getters (unchanged) ----
    public static String baseApiUrl() { return get(SystemVar.BASE_URL.getEnvKey(), "https://petstore.swagger.io/v2"); }
    public static String baseFilesApiUrl() { return get(SystemVar.FILES_BASE_URL.getEnvKey(), baseApiUrl()); }
    public static String acceptLang() { return get(SystemVar.ACCEPT_LANG.getEnvKey(), "en-US"); }
    public static boolean consoleLog() { return Boolean.parseBoolean(get(SystemVar.API_CONSOLE_LOG.getEnvKey(), "true")); }
    public static int retryMax() { return Integer.parseInt(get(SystemVar.API_RETRY_MAX.getEnvKey(), "2")); }
    public static String bearer() { return get(SystemVar.API_BEARER.getEnvKey(), ""); }

    private static String get(String key, String def) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) return env;
        return PROPS.getProperty(key, def);
    }
}
