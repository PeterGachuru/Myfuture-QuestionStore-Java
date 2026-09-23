package ke.co.myfuture.Myfuture.Commonauth.Install;

import lombok.Data;

@Data
public class InstallUpdate {
    String fcmToken;
    Integer appVersion;
    Long installId;
    String accountEmail;
    Long accountId;
}
