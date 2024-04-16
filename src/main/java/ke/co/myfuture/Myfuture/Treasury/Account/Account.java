package ke.co.myfuture.Myfuture.Treasury.Account;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import lombok.Data;

import javax.persistence.*;
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

	String ownershipType = "expense";//income or cash or personal

	String notes;

//	String targetType = "anyhow";//pledge//anyhow//weekly//monthly//annual

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

	Integer pinPriority  =  2;

	@Embedded
	AuditTrails auditTrails = new AuditTrails();
}