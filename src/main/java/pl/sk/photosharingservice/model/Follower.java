package pl.sk.photosharingservice.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name="followers")
@Data
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    @NonNull
    private Long userId;

    @Column(name="target_id")
    @NonNull
    private Long targetId;

    public Follower(Long user_id, Long target_id){
        this.userId=user_id;
        this.targetId=target_id;

    }
    public Follower(){}


}
