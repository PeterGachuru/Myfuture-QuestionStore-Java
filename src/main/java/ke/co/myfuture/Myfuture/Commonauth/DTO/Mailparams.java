package ke.co.myfuture.Myfuture.Commonauth.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Mailparams {
    private String email;
    private String subject;
    private String message;
}
