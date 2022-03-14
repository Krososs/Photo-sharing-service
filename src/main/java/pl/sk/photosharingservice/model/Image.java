package pl.sk.photosharingservice.model;

import lombok.*;
import javax.persistence.*;

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

    private String description;


    public Image(Long ownerId, String name, String description){
        this.ownerId =ownerId;
        this.name=name;
        this.description=description;
    }

    public Image(){}

}
