package ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EntityResponse<T> {
    private String message;
    private T entity;
    private Integer statusCode;
}
