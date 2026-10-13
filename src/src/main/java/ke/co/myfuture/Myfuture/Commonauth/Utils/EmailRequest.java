package ke.co.myfuture.Myfuture.Commonauth.Utils;

import java.util.Map;

public class EmailRequest {
    public String subject;
    public String body;
    public String[] toList;
    public String[] ccList;
    public String[] attachedFilePaths;
    public String fromName;
    public Map<String, String> mailConfig; // Contains mail host, port, username, password, etc.
}
