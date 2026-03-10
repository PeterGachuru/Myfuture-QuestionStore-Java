package ke.co.myfuture.Myfuture.QuestionStore.QuestionSettings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Utils.Response.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuestionSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;
    private String subCode;
    @Column(nullable = false)
    private String dataType;
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String settingValue;

    private Boolean active;

    private Boolean approved;
    private String createdBy;
    private String verifiedBy;
    private String updatedBy;
    private Date createdAt;
    private Date verifiedAt;
    private Date updatedAt;

    @Transient
    @JsonIgnore
    private Action action;

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
        this.approved = false;
        this.createdBy =  UserRequestContext.getCurrentUserName();
    }

    @PreUpdate
    public void preUpdate() {
        System.out.println("In persist");
        System.out.println("Action: "+action);
        if (action == null || action == Action.UPDATE) {
            System.out.println("force update");
            this.updatedAt = new Date();
            approved = false;
            this.updatedBy = UserRequestContext.getCurrentUserName();
        }
    }

    public void setVerified() {
        this.action = Action.VERIFY;
        this.updatedAt = new Date();
        this.approved = true;
        this.active= true;
        this.verifiedBy = UserRequestContext.getCurrentUserName();
    }

    private Date deletedAt;
    private String deletedBy;
    private Boolean deleted= false;

    public void setDeleted() {
        this.action = Action.DELETE;
        this.deletedAt = new Date();
        this.deleted = true;
        this.deletedBy = UserRequestContext.getCurrentUserName();
    }

    public static ArrayList<SettingHolder> getPossibleSettings() {
        ArrayList<SettingHolder> settingHolders = new ArrayList<>();
        settingHolders.add(new SettingHolder("latestQuestionUpdateId", "","long","Increments on every question insert/update"));
        return settingHolders;
    }

    @AllArgsConstructor
    static class SettingHolder {
        private String key;
        private String subKey;
        private String dataType;
        private String description;
    }

    @Transient
    static public String latestQuestionUpdateId = "latestQuestionUpdateId";
}
