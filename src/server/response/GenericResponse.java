package server.response;


import java.util.LinkedHashMap;
import java.util.Map;

public class GenericResponse {
    protected Map<String, Object> response = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends GenericResponse> T put(String key, Object value) {
        response.put(key, value);
        return (T) this;
    }

    public Map<String, Object> getMap() {
        return response;
    }
}
