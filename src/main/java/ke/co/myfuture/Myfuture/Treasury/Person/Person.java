package ke.co.myfuture.Myfuture.Treasury.Person;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import lombok.Data;

import javax.persistence.*;
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

	private boolean active=true;

	private Long systemUserId;
	private String systemUserName;

	@Transient
	List<PeopleGroup> peopleGroup;

	@Transient
	Long initialGroupId;

	private String notes;

	@Embedded
	AuditTrails auditTrails = new AuditTrails();

//	public AuditTrails.Retriever getAudits() {
//		AuditsService auditsService = ApplicationContextProvider.bean(AuditsService.class);
//		return auditsService.getAuditsForCart(id);
//	}
}