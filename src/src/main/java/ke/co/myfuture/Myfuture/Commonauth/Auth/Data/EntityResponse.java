package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityResponse<T> {
    private String message;
    private T entity;
    private Integer statusCode;
}
