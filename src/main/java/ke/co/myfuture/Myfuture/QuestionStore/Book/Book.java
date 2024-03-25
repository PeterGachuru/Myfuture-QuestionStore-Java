package ke.co.myfuture.Myfuture.QuestionStore.Book;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Book {
    @Id
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    public String name;
    @Column(nullable = false)
    public String contentType;
    @Column(nullable = false, unique = true)
    public String model;

    @Embedded()
    AuditTrails auditTrails = new AuditTrails();

    @Transient
    AuditTrails.Retriever audits;

    public void update(Book bookFromUser) {
        name = bookFromUser.getName();
        contentType = bookFromUser.contentType;
        model = bookFromUser.model;
    }
}