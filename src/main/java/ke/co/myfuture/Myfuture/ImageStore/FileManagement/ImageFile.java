package ke.co.myfuture.Myfuture.ImageStore.FileManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class ImageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    @Column(unique = true, nullable = false)
    public String code;
    @Lob
    @Column(nullable = false, length=1000000)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public byte[] imageContent;
    @Column(nullable = false)
    public String fileName;
    @Column(nullable = false)
    public String fileExtension;
    @Column(nullable = false)
    public String contentType;
    @Column(nullable = false)
    public long fileSize;

    @CreationTimestamp
    public Date createdAt =  new Date();
}