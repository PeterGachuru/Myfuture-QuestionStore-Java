package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Responses;

public class MessageResponse {

    private int statusCode;
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }
    public MessageResponse(int statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
