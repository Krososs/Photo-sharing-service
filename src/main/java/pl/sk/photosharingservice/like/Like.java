package pl.sk.photosharingservice.like;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "likes")
@Data
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @Column(name = "userId")
    private Long userId;
    @NonNull
    @Column(name = "imageId")
    private Long imageId;

    public Like(Long userId, Long imageId) {
        this.userId = userId;
        this.imageId = imageId;
    }

    public Like() {
    }

}
