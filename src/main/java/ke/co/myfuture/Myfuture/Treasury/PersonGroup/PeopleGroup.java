package ke.co.myfuture.Myfuture.Treasury.PersonGroup;


import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class PeopleGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    Boolean active = true;
    String name;
//    String label;
    String notes;
    @ManyToOne
    private PeopleGroup parent;
//    @OneToMany

    @Transient
    List<Person> members;

//    @OneToMany

    @Transient
    List<ContributionsPlan> plans;
    @Transient
    List<PeopleGroup> children;

    @Embedded
    AuditTrails auditTrails = new AuditTrails();
}
