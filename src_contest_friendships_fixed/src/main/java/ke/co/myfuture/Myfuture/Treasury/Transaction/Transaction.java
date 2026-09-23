package ke.co.myfuture.Myfuture.Treasury.Transaction;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.UserRequestContext;

import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntry;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranType;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.*;
import java.util.Date;
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

	private String externalTransactionCode;

	private String externalTransactionMessage;

	@Column(nullable = false)
	private String holderParticulars;

	@Column(nullable = false)
	private String oneOfTheAccounts;

	@ManyToOne
	ContributionsPlan contributionsPlan;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TransactionCategory category;

	@Transient
	Long planId;

	@Column(nullable = false)
	Date tranDate;

	Boolean reversal = false;

	Long reversalFor;

    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(cascade = CascadeType.ALL)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaction")
	private List<TranEntry> tranEntries;

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

	public Transaction(){

	}

	public Transaction(Transaction other) {
		this.amountInvolved = other.amountInvolved;
		this.status = other.status;
		this.notes = other.notes;
		this.holderParticulars = other.holderParticulars;
		this.oneOfTheAccounts = other.oneOfTheAccounts;
		this.contributionsPlan = other.contributionsPlan;  // Assuming shallow copy is sufficient
		this.category = other.category;
		this.planId = other.planId;
	}

	// Clone method
	public Transaction clone() {
		return new Transaction(this);
	}

	/**
	 * AuditTrails
	 */

	Date updatedAt;

//    @CreationTimestamp

	@Column(updatable = false, nullable = false)
	Date createdAt;

	Date deletedAt;

	Boolean deletedFlag = false;

	@Column(nullable = false)
	String createdBy;

	public void delete() {
		this.deletedAt = new Date();
		this.deletedFlag = true;
	}

	@PrePersist
	public void prePersist() {
		Date now = new Date();
		this.createdAt = now;
		this.updatedAt = now;
		this.createdBy = UserRequestContext.getCurrentUserName();
		if (UserRequestContext.getCurrentUserName() == null)
			this.createdBy = "Internal";
	}
	@PreUpdate
	public void preUpdate() {
		this.updatedAt = new Date();
	}

	static public interface Retriever {
		String getUpdatedAt();
		String getCreatedAt();

		String getCreatedBy();

		Boolean getDeletedFlag();
	}
}