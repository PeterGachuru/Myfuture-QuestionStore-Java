package ke.co.myfuture.Myfuture.Commonauth.Utils;

public class EmailResponse {
    private boolean success;
    private String message;
    private String debug;

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getDebug() {
        return debug;
    }
    public void setDebug(String debug) {
        this.debug = debug;
    }
}
