package utils.request;

import config.Config;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.internal.collections.Pair;
import utils.enums.HttpHeader;
import utils.enums.HttpMethod;
import utils.enums.HttpStatusGroup;
import utils.enums.MediaType;
import utils.request.exception.HttpsException;
import utils.request.path.IPath;

import java.io.File;
import java.util.*;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;
import static utils.AllureUtils.*;

public class HttpRequest {

    private final String baseApiUrl = Config.baseApiUrl();
    private final String filesApiUrl = Config.baseFilesApiUrl();
    private final boolean consoleLogEnabled = Config.consoleLog();

    public HttpRequest() {
        RestAssured.config = RestAssured.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
    }

    public String getRequest(Headers customHeaders, IPath path, String... pathParams) {
        return sendRequest(HttpMethod.GET, baseApiUrl, customHeaders, null, path, pathParams);
    }
    public String getBodyRequest(Headers customHeaders, Object requestBody, IPath path, String... pathParams) {
        return sendRequest(HttpMethod.GET, baseApiUrl, customHeaders, requestBody, path, pathParams);
    }
    public String postRequest(Headers customHeaders, Object requestBody, IPath path, String... pathParams) {
        return sendRequest(HttpMethod.POST, baseApiUrl, customHeaders, requestBody, path, pathParams);
    }
    public String putRequest(Headers customHeaders, Object requestBody, IPath path, String... pathParams) {
        return sendRequest(HttpMethod.PUT, baseApiUrl, customHeaders, requestBody, path, pathParams);
    }
    public String deleteRequest(Headers customHeaders, IPath path, String... pathParams) {
        return sendRequest(HttpMethod.DELETE, baseApiUrl, customHeaders, null, path, pathParams);
    }

    public String postRequestForUploadFile(
            final String fileToken,
            final List<Pair<String, File>> filePairsList,
            final List<Pair<String, String>> stringPairsList,
            String endpoint
    ) {
        RequestSpecification requestSpecification = given()
                .header(HttpHeader.CONTENT_TYPE.key, MediaType.APPLICATION_JSON.value)
                .header(HttpHeader.AUTHORIZATION.key, "Bearer " + fileToken)
                .contentType(MediaType.MULTIPART_FORM_DATA.value);

        if (filePairsList != null) filePairsList.forEach(pair -> requestSpecification.multiPart(pair.first(), pair.second()));
        if (stringPairsList != null) stringPairsList.forEach(pair -> requestSpecification.multiPart(pair.first(), pair.second()));
        if (consoleLogEnabled) requestSpecification.log().all();

        Response response = requestSpecification.when().post(filesApiUrl + endpoint);
        String responseBody = response.then().extract().asString();

        attach("POST multipart " + endpoint, null, response, responseBody);

        if (!utils.enums.HttpStatusGroup.SUCCESS_CODES.contains(response.statusCode())) {
            throw new HttpsException("Bad request: expected = " + HttpStatusGroup.SUCCESS_CODES + ", actual = "
                    + response.statusCode() + "\nError message:\n" + responseBody);
        }
        return responseBody;
    }

    private String sendRequest(HttpMethod httpMethod,
                               String baseUrl,
                               Headers customHeaders,
                               Object requestBody,
                               IPath pathTemplate,
                               String... pathParams) {

        String formattedPath = formatPath(pathTemplate, pathParams);

        RequestSpecification requestSpecification = given()
                .config(RestAssured.config().sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation()))
                .headers(mergedHeaders(customHeaders))
                .contentType(MediaType.APPLICATION_JSON.value);

        if (requestBody != null) requestSpecification.body(requestBody);
        if (consoleLogEnabled) requestSpecification.log().all();

        Supplier<Response> invokeSupplier = () -> invoke(httpMethod, requestSpecification, baseUrl + formattedPath);

        int attemptIndex = 0;
        int maximumAttempts = Math.max(0, Config.retryMax());
        long backoffMillis = 500;
        Response response;

        while (true) {
            attemptIndex++;
            response = invokeSupplier.get();

            if (consoleLogEnabled) {
                System.out.println(httpMethod + " " + pathTemplate.getDescription() + " -> " + response.statusCode());
            }

            if (!HttpStatusGroup.RETRYABLE_CODES.contains(response.statusCode()) || attemptIndex > maximumAttempts) break;

            sleep(backoffMillis);
            backoffMillis = Math.min(backoffMillis * 2, 4000);
        }

        String requestBodyMasked = safeString(requestBody);
        String responseBody = response.then().extract().asString();
        attach(httpMethod + " " + formattedPath, requestBodyMasked, response, responseBody);

        String contentTypeHeader = Optional.ofNullable(response.getHeader(HttpHeader.CONTENT_TYPE.key)).orElse("");
        boolean looksLikeHtml = contentTypeHeader.contains(MediaType.TEXT_HTML.value) || responseBody.startsWith("<!DOCTYPE");
        String hint = looksLikeHtml ? "\nHint: Response is HTML — check BASE_URL vs endpoint." : "";

        if (!HttpStatusGroup.SUCCESS_CODES.contains(response.statusCode())) {
            throw new HttpsException("Bad request: expected status_code = " + HttpStatusGroup.SUCCESS_CODES +
                    ", actual = " + response.statusCode() + "\nError message:\n" + responseBody + hint);
        }
        return responseBody;
    }

    /** GET with query parameters. */
    public String getWithQuery(Headers customHeaders, IPath pathTemplate,
                               Map<String, Object> queryParams, String... pathParams) {
        String formattedPath = formatPath(pathTemplate, pathParams);
        RequestSpecification requestSpecification = given()
                .config(RestAssured.config().sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation()))
                .headers(mergedHeaders(customHeaders))
                .contentType(MediaType.APPLICATION_JSON.value);

        if (queryParams != null && !queryParams.isEmpty()) requestSpecification.queryParams(queryParams);
        if (consoleLogEnabled) requestSpecification.log().all();

        Response response = requestSpecification.get(baseApiUrl + formattedPath);
        String responseBody = response.then().extract().asString();
        attach("GET " + formattedPath, "(no body)", response, responseBody);

        if (!HttpStatusGroup.SUCCESS_CODES.contains(response.statusCode())) {
            throw new HttpsException("Bad request: expected status_code = " + HttpStatusGroup.SUCCESS_CODES +
                    ", actual = " + response.statusCode() + "\nError message:\n" + responseBody);
        }
        return responseBody;
    }

    private Map<String, Object> mergedHeaders(Headers customHeaders) {
        Map<String, Object> merged = new LinkedHashMap<>();
        merged.put(HttpHeader.ACCEPT_LANGUAGE.key, config.Config.acceptLang());
        merged.put(HttpHeader.CONTENT_TYPE.key, MediaType.APPLICATION_JSON.value);
        String bearerToken = config.Config.bearer();
        if (bearerToken != null && !bearerToken.isBlank()) merged.put(HttpHeader.AUTHORIZATION.key, "Bearer " + bearerToken);
        if (customHeaders != null && customHeaders.getSize() != 0) {
            String[] headerKeyValue = customHeaders.getHeader();
            merged.put(headerKeyValue[0], headerKeyValue[1]);
        }
        return merged;
    }

    private Response invoke(HttpMethod httpMethod, RequestSpecification requestSpecification, String url) {
        return switch (httpMethod) {
            case POST    -> requestSpecification.post(url);
            case PUT     -> requestSpecification.put(url);
            case PATCH   -> requestSpecification.patch(url);
            case DELETE  -> requestSpecification.delete(url);
            case OPTIONS -> requestSpecification.options(url);
            default      -> requestSpecification.get(url);
        };
    }

    private static String formatPath(IPath path, String... pathParams) {
        String formatted = path.url();
        for (String param : pathParams) formatted = formatted.replaceFirst("%s", param);
        return formatted;
    }

    private static void sleep(long milliseconds) {
        try { Thread.sleep(milliseconds); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new RuntimeException(e); }
    }

    private static String safeString(Object object) { return (object == null) ? "(no body)" : String.valueOf(object); }

    private void attach(String title, String requestBody, Response response, String responseBody) {
        try {
            String maskedRequest = Sensitive.mask(requestBody);
            String maskedResponse = Sensitive.mask(responseBody);
            addAttachmentToReport("HTTP: " + title, getAllureReportMessage(response, maskedResponse, maskedRequest, title));
        } catch (Throwable ignored) { }
    }

    private static final class Sensitive {
        private static String mask(String text) {
            if (text == null) return null;
            return text.replaceAll("(?i)Bearer\s+[A-Za-z0-9._-]+", "Bearer ****")
                    .replaceAll("([\\w.%+-])([\\w.%+-]*)(@[^\\s\"']+)", "$1***$3");
        }
    }
}
