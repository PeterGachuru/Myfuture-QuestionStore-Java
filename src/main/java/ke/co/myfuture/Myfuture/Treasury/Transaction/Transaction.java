package ke.co.myfuture.Myfuture.Treasury.Transaction;


import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;

import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntry;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranType;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	private Double amountInvolved;

	private String status = "NORMAL";

	private String notes;

	@ManyToOne
	ContributionsPlan contributionsPlan;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TransactionCategory category;

	@Transient
	Long planId;

    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(cascade = CascadeType.ALL)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaction")
	private List<TranEntry> tranEntries;

	@Embedded
	AuditTrails auditTrails = new AuditTrails();

	public boolean balances() {
		double totalDebits = 0.0;
		double totalCredits = 0.0;

		for (TranEntry tranEntry: tranEntries) {
			if (tranEntry.getTranType() == TranType.CREDIT)
				totalCredits += tranEntry.getAmount();
			if (tranEntry.getTranType() == TranType.DEBIT)
				totalDebits += tranEntry.getAmount();
		}

		amountInvolved = totalCredits;

        return totalCredits == totalDebits;
    }
}