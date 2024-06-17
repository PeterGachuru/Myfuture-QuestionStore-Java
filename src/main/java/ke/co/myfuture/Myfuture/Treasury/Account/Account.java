package ke.co.myfuture.Myfuture.Treasury.Account;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TransactionCategory;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	private Double balance = 0.0;

	String name;

	String status = "ACTIVE";

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AccountOwnershipType ownershipType;

//	String ownershipType = "expense";//income or cash or personal

	String notes;

//	String targetType = "anyhow";//pledge//anyhow//weekly//monthly//annual

	@NotNull
	Double targetAmount = 0.0;

	Date promisedDate;

	@Transient
	Long planId;

	@Transient
	Long personId;

	@Transient
	Long groupId;

	@ManyToOne
	private Person owner;

	@ManyToOne
	ContributionsPlan contributionsPlan;

	@ManyToOne
	@JoinColumn(nullable = false)
	PeopleGroup peopleGroup;

	Integer pinPriority  =  1;

	@Embedded
	AuditTrails auditTrails = new AuditTrails();

	public void update(Account account) {
		this.name = account.name;
		this.ownershipType = account.ownershipType;
		this.notes = account.notes;
		this.promisedDate = account.promisedDate;
		this.targetAmount = account.targetAmount;
		this.pinPriority = account.pinPriority;
	}
}