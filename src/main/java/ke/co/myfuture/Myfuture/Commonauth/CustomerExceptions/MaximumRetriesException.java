package ke.co.myfuture.Myfuture.Commonauth.CustomerExceptions;

public class MaximumRetriesException extends Exception {
    public MaximumRetriesException(){
        super("Maximum retries reached. Kindly retry again later");
    }
}
