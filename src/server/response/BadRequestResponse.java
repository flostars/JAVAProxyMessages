package server.response;

import spark.Response;

public class BadRequestResponse extends GenericResponse {
    public BadRequestResponse(Response response) {
        response.status(400);
        this.put("status", "error");
    }
}
