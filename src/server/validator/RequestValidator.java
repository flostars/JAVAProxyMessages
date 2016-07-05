package server.validator;

import spark.Request;

import java.util.LinkedList;
import java.util.List;

public class RequestValidator {
    protected Request request;
    protected List<String> requiredParams = new LinkedList<String>();
    protected List<String> errors = new LinkedList<String>();

    public RequestValidator(Request request) {
        this.request = request;
    }

    public void validate(){
        for (String requiredParam : requiredParams){
            String paramValue = this.request.queryParams(requiredParam);
            if ( paramValue == null || paramValue.trim().equals("")){
                this.errors.add("Required parameter " + requiredParam + " is missing.");
            }
        }
    }

    public boolean hasErrors(){
        return !errors.isEmpty();
    }

    public List<String> getErrors(){
        return  new LinkedList<>(errors);
    }
}
