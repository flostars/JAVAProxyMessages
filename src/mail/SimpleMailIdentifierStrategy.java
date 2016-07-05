package mail;


import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleMailIdentifierStrategy implements MailIdentifierStrategy {
    @Override
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String padMessage(String messageBody, String token) {
        return messageBody + "\n\n\n<<" + token + ">>";
    }

    @Override
    public String extractToken(String emailBody) {
        Pattern pattern = Pattern.compile("<<([a-z0-9\\-]{32,36})>>");
        Matcher matcher = pattern.matcher(emailBody);
        if (matcher.find()) {
            System.out.println(matcher.groupCount());
            return matcher.group(1);
        }

        return null;

    }
}
