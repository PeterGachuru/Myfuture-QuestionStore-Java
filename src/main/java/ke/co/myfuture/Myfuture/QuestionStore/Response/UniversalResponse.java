package ke.co.myfuture.Myfuture.QuestionStore.Response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UniversalResponse<T> {
    private String Status;
    private String message; //Saved succesfully
    private T entity; //{}
    private Integer statusCode; //201
}
