package utils.request.http;

import config.Config;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.internal.collections.Pair;
import utils.enums.HttpHeader;
import utils.enums.HttpStatusGroup;
import utils.enums.MediaType;
import utils.request.exception.HttpsException;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;
import static utils.AllureUtils.addAttachmentToReport;
import static utils.AllureUtils.getAllureReportMessage;

public class MultipartHttpClient {

    private final String filesApiUrl = Config.baseFilesApiUrl();
    private final boolean consoleLogEnabled = Config.consoleLog();

    public MultipartHttpClient() {
        RestAssured.config = RestAssured.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
    }

    public String postMultipart(final String fileToken,
                                final List<Pair<String, File>> filePairsList,
                                final List<Pair<String, String>> stringPairsList,
                                final String endpoint) {
        RequestSpecification spec = given()
                .header(HttpHeader.CONTENT_TYPE.getKey(), MediaType.APPLICATION_JSON.getValue())
                .header(HttpHeader.AUTHORIZATION.getKey(), "Bearer " + fileToken)
                .contentType(MediaType.MULTIPART_FORM_DATA.getValue());

        if (filePairsList != null) filePairsList.forEach(pair -> spec.multiPart(pair.first(), pair.second()));
        if (stringPairsList != null) stringPairsList.forEach(pair -> spec.multiPart(pair.first(), pair.second()));
        if (consoleLogEnabled) spec.log().all();

        Response response = spec.when().post(filesApiUrl + endpoint);
        String responseBody = response.then().extract().asString();

        attach("POST multipart " + endpoint, null, response, responseBody);

        if (!HttpStatusGroup.SUCCESS_CODES.contains(response.statusCode())) {
            throw new HttpsException("Bad request: expected = " + HttpStatusGroup.SUCCESS_CODES + ", actual = "
                    + response.statusCode() + "\nError message:\n" + responseBody);
        }
        return responseBody;
    }

    private void attach(String title, String requestBody, Response response, String responseBody) {
        try {
            addAttachmentToReport("HTTP: " + title, getAllureReportMessage(response, responseBody, requestBody, title));
        } catch (Throwable ignored) { }
    }
}