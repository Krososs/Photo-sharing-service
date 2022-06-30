package pl.sk.photosharingservice.image;

import lombok.*;
import org.hibernate.annotations.Formula;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="images")
@Data
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @Column(name="ownerId")
    private Long ownerId;
    @NonNull
    private String source;
    @NonNull
    private String name;
    @NonNull
    private Date releaseDate;
    @NonNull
    @Formula("(SELECT COUNT(1) FROM likes l WHERE l.image_id = id)")
    private Integer likes;

    private String description;

    public Image(Long ownerId, String name, String description ){
        this.ownerId =ownerId;
        this.name=name;
        this.description=description;
        this.likes=0;
        this.releaseDate= new Date();
    }

    public Image(){}

}
