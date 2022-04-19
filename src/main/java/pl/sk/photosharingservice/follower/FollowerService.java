package pl.sk.photosharingservice.follower;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowerService {

    private final FollowerRepository followerRepository;

    @Autowired
    public FollowerService(FollowerRepository followerRepository) {
        this.followerRepository = followerRepository;
    }

    public Boolean checkIfFollowed(Long userId, Long targetId){

        return followerRepository.checkIfFollowed(userId, targetId).isPresent();
    }

    public void handleFollow(Long userId, Long targetId){

        if(!followerRepository.checkIfFollowed(userId, targetId).isPresent())
            followerRepository.save(new Follower(userId, targetId));
        else
            followerRepository.Unfollow(userId,targetId);
    }

    public List<Follower> getFollowing(Long id){ return followerRepository.findByUserId(id); }
    public List<Follower> getFollowers(Long id){ return followerRepository.findByTargetId(id); }


}
