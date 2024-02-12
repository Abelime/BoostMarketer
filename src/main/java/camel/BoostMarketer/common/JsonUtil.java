package camel.BoostMarketer.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> stringToMap(String jsonString) throws IOException {
        return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
    }

    public static List<Map<String, Object>> stringToListOfMaps(String jsonString) throws IOException {
        return objectMapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>() {});
    }
}
