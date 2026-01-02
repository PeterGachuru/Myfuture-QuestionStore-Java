package ke.co.myfuture.Myfuture.Utils.Response;

import ke.co.myfuture.Myfuture.Commonauth.Install.Install;
import lombok.Data;

@Data
public class ApiSender<T> {
    private Long installId;
//    private User user;
    private T entity; //{}
}


