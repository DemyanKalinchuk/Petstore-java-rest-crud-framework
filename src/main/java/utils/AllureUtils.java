package utils;

import io.restassured.response.Response;
import io.qameta.allure.Allure;

public final class AllureUtils {
    private AllureUtils(){}
    public static void addAttachmentToReport(String name, String content) {
        try { Allure.addAttachment(name, content); } catch (Throwable ignored) {}
    }
    public static String getAllureReportMessage(Response response, String maskedResponse, String maskedRequest, String title) {
        StringBuilder builder = new StringBuilder();
        builder.append("Title: ").append(title).append("\n")
               .append("Status: ").append(response == null ? "?" : response.statusCode()).append("\n")
               .append("Request: ").append(maskedRequest == null ? "(no body)" : maskedRequest).append("\n")
               .append("Response: ").append(maskedResponse == null ? "" : maskedResponse).append("\n");
        return builder.toString();
    }
}
