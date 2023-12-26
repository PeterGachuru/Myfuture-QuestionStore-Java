package ke.co.myfuture.Myfuture.Commonauth.DTO;

public interface Analytics {
    public Integer getMatchedData();
    public Integer getUnmatchedData();
    public Integer getManualData();
    public Integer getExceptionData();
    public Integer getReversalData();
    public Integer getSpillover();
    public Double getSpilloverAmount();
    public Double getReversalAmount();
    public Double getManualAmount();
    public Double getExceptionAmount();
    public Double getTotalAmount();
    public Double getMatchedAmount();
    public Double getUnMatchedAmount();
    public Double getClosingBalance();
    public Double getOpeningBalance();
    public Double getDifference();
    public Integer getAllData();
    public String getReconDate();
    public String getCountry();
    public String getTelcom();
    public Boolean getDone();
    public String getCurrency();
}