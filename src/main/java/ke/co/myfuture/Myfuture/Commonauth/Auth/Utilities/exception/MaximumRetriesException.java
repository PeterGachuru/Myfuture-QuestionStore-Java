package ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.exception;

public class MaximumRetriesException extends Exception {
    public MaximumRetriesException(){
        super("Maximum retries reached. Kindly retry again later");
    }
}
