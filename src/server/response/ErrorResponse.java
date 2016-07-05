package server.response;

import spark.Response;

public class ErrorResponse extends GenericResponse {
    public ErrorResponse(Response response, Exception e) {
        response.status(500);
        this.put("status", "error");
        this.put("details", new String[]{e.getMessage()});
    }
}
