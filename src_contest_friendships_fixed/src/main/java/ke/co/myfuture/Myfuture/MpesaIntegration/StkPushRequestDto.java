package ke.co.myfuture.Myfuture.MpesaIntegration;


import lombok.Data;

@Data
public class StkPushRequestDto {
    private String phoneNumber;
    private Double amount;
    private String accountReference;

    private Long accountReferenceId;
    private String transactionDesc;

}
