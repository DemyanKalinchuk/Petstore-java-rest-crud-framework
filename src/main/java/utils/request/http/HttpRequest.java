package utils.request.http;

import io.restassured.response.Response;
import org.testng.internal.collections.Pair;
import utils.request.Headers;
import utils.request.path.IPath;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Backward-compatible facade. Internally delegates to two focused clients:
 * - JsonHttpClient  (JSON requests, retries, raw/strict)
 * - MultipartHttpClient (multipart uploads)
 *
 * Migrate step classes to JsonHttpClient/MultipartHttpClient directly when convenient.
 */
public class HttpRequest {

    private final JsonHttpClient json = new JsonHttpClient();
    private final MultipartHttpClient multipart = new MultipartHttpClient();

    public String getRequest(Headers customHeaders, IPath path, String... pathParams) {
        return json.getRequest(customHeaders, path, pathParams);
    }

    public String getBodyRequest(Headers customHeaders, Object requestBody, IPath path, String... pathParams) {
        return json.getBodyRequest(customHeaders, requestBody, path, pathParams);
    }

    public String postRequest(Headers customHeaders, Object requestBody, IPath path, String... pathParams) {
        return json.postRequest(customHeaders, requestBody, path, pathParams);
    }

    public String putRequest(Headers customHeaders, Object requestBody, IPath path, String... pathParams) {
        return json.putRequest(customHeaders, requestBody, path, pathParams);
    }

    public String deleteRequest(Headers customHeaders, IPath path, String... pathParams) {
        return json.deleteRequest(customHeaders, path, pathParams);
    }

    public String getWithQuery(Headers customHeaders, IPath pathTemplate, Map<String, Object> queryParams, String... pathParams) {
        return json.getWithQuery(customHeaders, pathTemplate, queryParams, pathParams);
    }

    public Response getRaw(Headers customHeaders, IPath pathTemplate, Map<String, Object> queryParams, String... pathParams) {
        return json.getRaw(customHeaders, pathTemplate, queryParams, pathParams);
    }

    public Response deleteRaw(Headers customHeaders, IPath pathTemplate, String... pathParams) {
        return json.deleteRaw(customHeaders, pathTemplate, pathParams);
    }

    public String postRequestForUploadFile(String fileToken,
                                           List<Pair<String, File>> filePairsList,
                                           List<Pair<String, String>> stringPairsList,
                                           String endpoint) {
        return multipart.postMultipart(fileToken, filePairsList, stringPairsList, endpoint);
    }
}