package ke.co.myfuture.Myfuture.Utils.Response;


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

    private ResponseType responseType;
    private Integer currentPage;
    private Integer totalItems;
    private Integer totalPages;
    private Integer statusCode; //201
}
