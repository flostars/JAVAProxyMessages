package server.validator;

import server.model.PlatformCode;
import spark.Request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import static server.params.SendMessageParams.*;

public class SendMessageRequestValidator extends RequestValidator {

    public SendMessageRequestValidator(Request request) {
        super(request);

        requiredParams.addAll(Arrays.asList(PARAM_PLATFORM_CODE, PARAM_PLATFORM_LISTING_ID,
                PARAM_MESSAGE_SUBJECT, PARAM_MESSAGE_BODY, PARAM_WEBHOOK_URL));
    }

    public void validate(){
        super.validate();
        ensureWebhookIsValidURL();
        ensurePlatformIsSupported();
    }

    private void ensurePlatformIsSupported(){
        boolean platformIsSupported = PlatformCode.valueOf(request.queryParams(PARAM_PLATFORM_CODE)) != null;
        if (!platformIsSupported){
            errors.add("Unsupported " + PARAM_PLATFORM_CODE + ".");
        }
    }

    private void ensureWebhookIsValidURL(){
        String webhookUrlParam = this.request.queryParams(PARAM_WEBHOOK_URL);
        try {
            new URL(webhookUrlParam);
        } catch (MalformedURLException e) {
            errors.add("Parameter " + PARAM_WEBHOOK_URL + " is not a valid URL.");
        }
    }
}
