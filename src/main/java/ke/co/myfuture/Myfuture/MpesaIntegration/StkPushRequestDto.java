package ke.co.myfuture.Myfuture.MpesaIntegration;


public class StkPushRequestDto {
    private String phoneNumber;
    private Double amount;
    private String accountReference;
    private String transactionDesc;

    public StkPushRequestDto() {}
    // getters and setters
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getAccountReference() { return accountReference; }
    public void setAccountReference(String accountReference) { this.accountReference = accountReference; }
    public String getTransactionDesc() { return transactionDesc; }
    public void setTransactionDesc(String transactionDesc) { this.transactionDesc = transactionDesc; }
}
