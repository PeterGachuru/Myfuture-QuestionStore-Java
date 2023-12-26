package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Requests;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SingleLoanRepayment {
    public String account;
    public Double amount;
    public String transactionParticulars;
    public String partTranType;
    public Date transactionDate;
    public String solCode;
    public String postedBy;
    public Date postedTime;
    public String postedFlag;
}
