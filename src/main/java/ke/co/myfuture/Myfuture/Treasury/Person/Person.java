package ke.co.myfuture.Myfuture.Treasury.Person;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
@Data
public class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;
	private String name;
	private String phoneNumber;
	private String email;
	private String role;
	Boolean emailVerified = false;
	VerificationStatus emailVerificationAttemptStatus;

	private boolean active=true;

	@Transient
	List<PeopleGroup> peopleGroup;

	@Transient
	Long initialGroupId;

	private String notes;


	/**
	 * AuditTrails
	 */

	Date updatedAt;

//    @CreationTimestamp

	@Column(updatable = false)
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

	public void update(Person person) {
		email = person.email;
		phoneNumber = person.phoneNumber;
		notes = person.notes;
		role = person.role;
		name = person.name;
	}

	static public interface Retriever {
		String getUpdatedAt();
		String getCreatedAt();

		String getCreatedBy();

		Boolean getDeletedFlag();
	}
}