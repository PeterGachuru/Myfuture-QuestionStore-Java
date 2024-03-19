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
    private Long id;
    @Column(unique = true, nullable = false)
    private String code;
    @Lob
    @Column(nullable = false, length=1000000)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] imageContent;
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private String fileExtension;
    @Column(nullable = false)
    private String contentType;
    @Column(nullable = false)
    private long fileSize;

    private Integer width;
    private Integer height;

    private String description;

    private String tags;

    @CreationTimestamp
    public Date createdAt =  new Date();
}