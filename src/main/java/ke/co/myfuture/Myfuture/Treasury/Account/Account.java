package ke.co.myfuture.Myfuture.Treasury.Account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.PeriodicContributionAnalysis.PeriodicContributionAnalysis;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;


	@Column(nullable = false)
	private Double balance = 0.0;

	String name;
	@Enumerated(EnumType.STRING)
	AccountStatus status = AccountStatus.PENDING;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AccountOwnershipType ownershipType;

	private String productCode;
	private String productName;

//	String ownershipType = "expense";//income or cash or personal

	String notes;

//	String targetType = "anyhow";//pledge//anyhow//weekly//monthly//annual

	@NotNull
	Double targetAmount = 0.0;

	Date startDate ;
	Date promisedDate;
	@JsonIgnore
	Date lastCalculationDate;

	@Transient
	Long planId;

	@Transient
	Long groupId;

	@Transient
	Long personId;

	@ManyToOne
	private Person owner;

	@ManyToOne
	ContributionsPlan contributionsPlan;

	@ManyToOne
	@JoinColumn(nullable = false)
	PeopleGroup peopleGroup;

	Integer pinPriority  =  1;

	@Transient
	List<PeriodicContributionAnalysis> periodicContributionAnalyses;

	Date lastAnalysisDate;
	String accountClosureReason;
	Date accountClosureDate;
	String accountClosedBy;

	public void update(Account account) {
		this.name = account.name;
		this.ownershipType = account.ownershipType;
		this.notes = account.notes;
		this.promisedDate = account.promisedDate;
		this.startDate = account.startDate;
		this.targetAmount = account.targetAmount;
		this.pinPriority = account.pinPriority;
	}

	/**
	 * AuditTrails
	 */

	Date updatedAt;

	Date activatedAt;
	String activatedBy;

//    @CreationTimestamp

	@Column(updatable = false)
	Date createdAt;

	Date deletedAt;

	Boolean deletedFlag = false;

	@Column(nullable = false)
	String createdBy;

	String deletedBy;

	public void delete() {
		this.deletedAt = new Date();
		this.deletedFlag = true;
		this.deletedBy =  UserRequestContext.getCurrentUserName();
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

	public void setActive() {
		activatedBy = UserRequestContext.getCurrentUserName();
		activatedAt = new Date();
		status = AccountStatus.ACTIVE;
	}

	public void close(String closureReason) {
		this.accountClosureReason = closureReason;
		this.accountClosureDate = new Date();
		this.accountClosedBy = UserRequestContext.getCurrentUserName();
		this.status = AccountStatus.CLOSED;
	}

    static public interface Retriever {
		String getUpdatedAt();
		String getCreatedAt();

		String getCreatedBy();

		Boolean getDeletedFlag();
	}
}