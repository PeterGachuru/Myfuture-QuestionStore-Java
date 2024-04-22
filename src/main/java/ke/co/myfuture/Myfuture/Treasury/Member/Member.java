package ke.co.myfuture.Myfuture.Treasury.Member;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    PeopleGroup peopleGroup;

    @ManyToOne
    Person person;

    @Transient
    Long personId;
    @Transient
    Long groupId;

    @Embedded
    AuditTrails auditTrails = new AuditTrails();
}