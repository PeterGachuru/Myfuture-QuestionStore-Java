package ke.co.myfuture.Myfuture.Treasury.Demands.DemandBreakdown;

public enum DemandComponentType {
    PRINCIPAL("Loan Principal"),
    SHARES("Share Contribution"),
    SAVINGS("Savings"),
    INTEREST("Loan Interest"),
    FEE("Service Fee"),
    PENALTY("Penalty"),
    OTHER("Other");

    private final String label;

    DemandComponentType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

