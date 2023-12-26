package ke.co.myfuture.Myfuture.Commonauth.CustomerExceptions;

public class MakerCheckerFailException extends Exception{
    public MakerCheckerFailException(){
        super("You cannot verify a transaction you created");
    }
}
