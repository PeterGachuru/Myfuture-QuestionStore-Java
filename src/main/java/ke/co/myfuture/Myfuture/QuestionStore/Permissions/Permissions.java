package ke.co.myfuture.Myfuture.QuestionStore.Permissions;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"permission", "phone"})})
public class Permissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    Long permission;
    String phone;
}