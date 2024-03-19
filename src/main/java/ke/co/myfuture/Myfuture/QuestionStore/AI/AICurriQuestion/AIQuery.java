package ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion;

import ke.co.myfuture.Myfuture.Commonauth.ApplicationContextProvider;
import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Dukazote.AuditsService;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class AIQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String queryQuestion;
    @Column(nullable = false)
    private String queryPurpose;

//    @ManyToOne()
//    @JoinColumn(nullable = false)
    private Long subtopicId;

    @Lob
    private String aiResponse;

    private String aIModel;

    @Embedded()
    AuditTrails auditTrails = new AuditTrails();

    @Transient
    AuditTrails.Retriever audits;

    public AuditTrails.Retriever getAudits() {
        AuditsService auditsService = ApplicationContextProvider.bean(AuditsService.class);
        return auditsService.getAuditsForInventory(id);
    }
}
