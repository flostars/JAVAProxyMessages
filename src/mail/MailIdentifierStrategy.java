package mail;

import java.util.UUID;

/**
 * Created by irina on 3/7/16.
 */
public interface MailIdentifierStrategy {
    public String generateToken();

    public String padMessage(String messageBody, String token);

    public String extractToken(String emailBody);
}
