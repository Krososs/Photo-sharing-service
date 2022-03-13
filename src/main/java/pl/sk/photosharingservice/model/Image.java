package pl.sk.photosharingservice.model;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
@Table(name="images")
@Getter
@Setter
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

    private String description;


    public Image(Long ownerId, String name){
        this.ownerId =ownerId;
        this.name=name;
    }

    public Image(){}

}
