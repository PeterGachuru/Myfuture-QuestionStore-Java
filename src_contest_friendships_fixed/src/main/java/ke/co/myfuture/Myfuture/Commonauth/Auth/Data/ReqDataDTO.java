package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReqDataDTO {
    private String accountNo;
    private String currency;
    private String amount;
    private String tranCode;
    private Date tranDate;
    private String rrn;
}
