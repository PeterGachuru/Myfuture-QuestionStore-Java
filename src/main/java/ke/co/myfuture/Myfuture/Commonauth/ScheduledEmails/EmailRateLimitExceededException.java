package ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails;

public class EmailRateLimitExceededException extends RuntimeException {
    public EmailRateLimitExceededException(String message) {
        super(message);
    }
}
