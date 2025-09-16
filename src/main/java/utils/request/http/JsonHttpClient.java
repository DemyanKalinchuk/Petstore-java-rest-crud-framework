package utils.request.http;

import config.Config;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.enums.HttpHeader;
import utils.enums.HttpMethod;
import utils.enums.HttpStatusGroup;
import utils.enums.MediaType;
import utils.request.Headers;
import utils.request.exception.HttpsException;
import utils.request.path.IPath;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;
import static utils.AllureUtils.addAttachmentToReport;
import static utils.AllureUtils.getAllureReportMessage;
import static utils.helpers.WaitHelper.justWait;

public class JsonHttpClient {

    private final String baseApiUrl = Config.baseApiUrl();
    private final boolean consoleLogEnabled = Config.consoleLog();
    private final long defaultTimeoutInMilliseconds = 1000L;

    private static final Set<Integer> RETRYABLE_CODES = HttpStatusGroup.RETRYABLE_CODES;
    private static final Set<Integer> SUCCESS_CODES = HttpStatusGroup.SUCCESS_CODES;

    public JsonHttpClient() {
        RestAssured.config = RestAssured.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
    }

    // ---------- Public JSON API (String responses) ----------

    public String getRequest(Headers customHeaders, IPath pathTemplate, String... pathParams) {
        return sendRequest(HttpMethod.GET, baseApiUrl, customHeaders, null, pathTemplate, null, pathParams);
    }

    public String getBodyRequest(Headers customHeaders, Object requestBody, IPath pathTemplate, String... pathParams) {
        return sendRequest(HttpMethod.GET, baseApiUrl, customHeaders, requestBody, pathTemplate, null, pathParams);
    }

    public String getWithQuery(Headers customHeaders, IPath pathTemplate,
                               Map<String, Object> queryParams, String... pathParams) {
        return sendRequest(HttpMethod.GET, baseApiUrl, customHeaders, null, pathTemplate, queryParams, pathParams);
    }

    public String postRequest(Headers customHeaders, Object requestBody, IPath pathTemplate, String... pathParams) {
        return sendRequest(HttpMethod.POST, baseApiUrl, customHeaders, requestBody, pathTemplate, null, pathParams);
    }

    public String putRequest(Headers customHeaders, Object requestBody, IPath pathTemplate, String... pathParams) {
        return sendRequest(HttpMethod.PUT, baseApiUrl, customHeaders, requestBody, pathTemplate, null, pathParams);
    }

    public String deleteRequest(Headers customHeaders, IPath pathTemplate, String... pathParams) {
        return sendRequest(HttpMethod.DELETE, baseApiUrl, customHeaders, null, pathTemplate, null, pathParams);
    }

    /** Raw GET (no success check) — for negative flows. */
    public Response getRaw(Headers customHeaders, IPath pathTemplate,
                           Map<String, Object> queryParams, String... pathParams) {
        final String formattedPath = formatPath(pathTemplate, pathParams);
        RequestSpecification spec = baseSpec(customHeaders);
        if (queryParams != null && !queryParams.isEmpty()) spec.queryParams(queryParams);
        if (consoleLogEnabled) spec.log().all();

        Response response = spec.get(baseApiUrl + formattedPath);
        try {
            String responseBody = response.then().extract().asString();
            attach("RAW GET " + formattedPath, "(no body)", response, responseBody);
        } catch (Throwable ignored) {}
        return response;
    }

    /** Raw DELETE (no success check) — for negative flows. */
    public Response deleteRaw(Headers customHeaders, IPath pathTemplate, String... pathParams) {
        final String formattedPath = formatPath(pathTemplate, pathParams);
        RequestSpecification spec = baseSpec(customHeaders);
        if (consoleLogEnabled) spec.log().all();

        Response response = spec.delete(baseApiUrl + formattedPath);
        try {
            String responseBody = response.then().extract().asString();
            attach("RAW DELETE " + formattedPath, "(no body)", response, responseBody);
        } catch (Throwable ignored) {}
        return response;
    }

    // ---------- Core request w/ retry & Allure attachment ----------

    private String sendRequest(HttpMethod httpMethod,
                               String baseUrl,
                               Headers customHeaders,
                               Object requestBody,
                               IPath pathTemplate,
                               Map<String, Object> queryParams,
                               String... pathParams) {

        final String formattedPath = formatPath(pathTemplate, pathParams);

        RequestSpecification spec = baseSpec(customHeaders);
        if (queryParams != null && !queryParams.isEmpty()) spec.queryParams(queryParams);
        if (requestBody != null) spec.body(requestBody);
        if (consoleLogEnabled) spec.log().all();

        Supplier<Response> call = () -> invoke(httpMethod, spec, baseUrl + formattedPath);

        int attempt = 0;
        int maxAttempts = Math.max(0, Config.retryMax());
        Response response;

        while (true) {
            attempt++;
            response = call.get();

            if (consoleLogEnabled) {
                System.out.println(httpMethod + " " + pathTemplate.getDescription() + " -> " + response.statusCode());
            }

            if (!RETRYABLE_CODES.contains(response.statusCode()) || attempt > maxAttempts) {
                break;
            }

            // Gentle backoff for conflict/ratelimit
            if (isTransientFailure(response.statusCode())) {
                justWait(defaultTimeoutInMilliseconds);
            }
        }

        String requestBodyMasked = safeString(requestBody);
        String responseBody = response.then().extract().asString();
        attach(httpMethod + " " + formattedPath, requestBodyMasked, response, responseBody);

        String contentType = Optional.ofNullable(response.getHeader(HttpHeader.CONTENT_TYPE.getKey())).orElse("");
        boolean looksLikeHtml = contentType.contains(MediaType.TEXT_HTML.getValue()) || responseBody.startsWith("<!DOCTYPE");
        String hint = looksLikeHtml ? "\nHint: Response is HTML — check BASE_URL vs endpoint." : "";

        if (!SUCCESS_CODES.contains(response.statusCode())) {
            throw new HttpsException("Bad request: expected status_code = " + SUCCESS_CODES +
                    ", actual = " + response.statusCode() + "\nError message:\n" + responseBody + hint);
        }

        return responseBody;
    }

    private RequestSpecification baseSpec(Headers customHeaders) {
        return given()
                .config(RestAssured.config().sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation()))
                .headers(mergedHeaders(customHeaders))
                .contentType(MediaType.APPLICATION_JSON.getValue());
    }

    private static boolean isTransientFailure(int status) {
        return status == 409 /*CONFLICT*/ || status == 410 /*GONE*/ || status == 429 /*TOO_MANY_REQUESTS*/ || status == 404 /*NOT_FOUND*/;
    }

    private Map<String, Object> mergedHeaders(Headers customHeaders) {
        Map<String, Object> merged = new LinkedHashMap<>();
        merged.put(HttpHeader.ACCEPT_LANGUAGE.getKey(), Config.acceptLang());
        merged.put(HttpHeader.CONTENT_TYPE.getKey(), MediaType.APPLICATION_JSON.getValue());

        String bearerToken = Config.bearer();
        if (bearerToken != null && !bearerToken.isBlank()) {
            merged.put(HttpHeader.AUTHORIZATION.getKey(), "Bearer " + bearerToken);
        }

        if (customHeaders != null && customHeaders.getSize() != 0) {
            String[] headerKeyValue = customHeaders.getHeader();
            merged.put(headerKeyValue[0], headerKeyValue[1]);
        }
        return merged;
    }

    private static Response invoke(HttpMethod method, RequestSpecification spec, String url) {
        return switch (method) {
            case POST    -> spec.post(url);
            case PUT     -> spec.put(url);
            case PATCH   -> spec.patch(url);
            case DELETE  -> spec.delete(url);
            case OPTIONS -> spec.options(url);
            default      -> spec.get(url);
        };
    }

    private static String formatPath(IPath path, String... pathParams) {
        String formatted = path.url();
        for (String param : pathParams) formatted = formatted.replaceFirst("%s", param);
        return formatted;
    }

    private static String safeString(Object object) { return (object == null) ? "(no body)" : String.valueOf(object); }

    private void attach(String title, String requestBody, Response response, String responseBody) {
        try {
            String maskedRequest = Sensitive.mask(requestBody);
            String maskedResponse = Sensitive.mask(responseBody);
            addAttachmentToReport("HTTP: " + title, getAllureReportMessage(response, maskedResponse, maskedRequest, title));
        } catch (Throwable ignored) { }
    }

    /** Minimal masking to avoid leaking tokens/emails in reports. */
    private static final class Sensitive {
        private static String mask(String text) {
            if (text == null) return null;
            return text
                    .replaceAll("(?i)Bearer\\s+[A-Za-z0-9._-]+", "Bearer ****")
                    .replaceAll("([\\w.%+-])([\\w.%+-]*)(@[^\\s\"']+)", "$1***$3");
        }
    }
}