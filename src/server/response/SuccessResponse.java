package server.response;

import spark.Response;

public class SuccessResponse extends GenericResponse{
    public SuccessResponse(Response response) {
        response.status(200);
        this.put("status", "success");
    }
}
