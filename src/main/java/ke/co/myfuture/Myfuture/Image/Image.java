package ke.co.myfuture.Myfuture.Image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Image {
    @Id
    @Column(name = "id", nullable = false)
    public Long id;

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