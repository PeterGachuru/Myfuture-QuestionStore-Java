package ke.co.myfuture.Myfuture.Commonauth.Auth.Role;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Converter.AccessRightsConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@ToString
@Getter @Setter
@EqualsAndHashCode(of = {"id"})
@DynamicUpdate
@Entity
@Slf4j
@Table(name = "role_config", uniqueConstraints = {
        @UniqueConstraint(name = "role_id", columnNames = {"id"})
})
public class RoleConfig implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private Integer status;

    @Convert(converter = AccessRightsConverter.class)
    @Column(name = "access_rights", nullable = false, columnDefinition = "text")
    private List<AccessRight> accessRights;

    @UpdateTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "create_date", nullable = false)
    private Timestamp creationDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "update_date", nullable = false)
    private Timestamp updateDate;

    @PostLoad
    void fillTransient() {
//        log.info("Access rights are: {}", accessRights);
    }
}
