package utils.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonHelper {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private JsonHelper(){}

    public static JsonNode parse(String json) {
        try {
            return MAPPER.readTree(json == null ? "{}" : json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON provided", e);
        }
    }

    public static String getString(JsonNode node, String fieldName) {
        return node.path(fieldName).isMissingNode() || node.path(fieldName).isNull() ? null : node.path(fieldName).asText();
    }
    public static Long getLong(JsonNode node, String fieldName) {
        return node.path(fieldName).isMissingNode() || node.path(fieldName).isNull() ? null : node.path(fieldName).asLong();
    }
    public static Integer getInt(JsonNode node, String fieldName) {
        return node.path(fieldName).isMissingNode() || node.path(fieldName).isNull() ? null : node.path(fieldName).asInt();
    }
    public static boolean has(JsonNode node, String fieldName) {
        return !node.path(fieldName).isMissingNode() && !node.path(fieldName).isNull();
    }
}