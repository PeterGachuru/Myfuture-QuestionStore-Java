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
    private String status;
    private String message; // Saved successfully
    private T entity; // {}

    private ResponseType responseType;
    private Integer currentPage;
    private Integer totalItems;
    private Integer totalPages;
    private Integer statusCode; // 201

    // Constructor with statusCode and message only
    public UniversalResponse(Integer statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    // Constructor with statusCode, entity, and message only
    public UniversalResponse(Integer statusCode, T entity, String message) {
        this.statusCode = statusCode;
        this.entity = entity;
        this.message = message;
    }
}
