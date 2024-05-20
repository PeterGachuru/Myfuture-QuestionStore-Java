package ke.co.myfuture.Myfuture.Treasury.GroupAccess;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class GroupAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    PeopleGroup peopleGroup;

    @ManyToOne
    @JoinColumn(nullable = false)
    Person person;

    @Column(nullable = false)
    String username;

    @Column(nullable = false)
    Long loginUserId;

    @Transient
    Long groupId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    GroupAccessRole role;

    @Embedded
    AuditTrails auditTrails = new AuditTrails();
}
