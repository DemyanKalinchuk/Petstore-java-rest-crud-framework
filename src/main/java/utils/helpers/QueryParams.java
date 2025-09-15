package utils.helpers;

import utils.enums.QueryParamKey;
import java.util.LinkedHashMap;
import java.util.Map;

public final class QueryParams {
    private QueryParams(){}
    public static Map<String, Object> forLogin(String username, String password) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put(QueryParamKey.USERNAME.key, username);
        query.put(QueryParamKey.PASSWORD.key, password);
        return query;
    }
}
