package pl.sk.photosharingservice.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.sk.photosharingservice.model.Follower;
import pl.sk.photosharingservice.repository.FollowerRepository;

import java.util.List;

@Service
public class FollowerService {

    private final FollowerRepository followerRepository;

    @Autowired
    public FollowerService(FollowerRepository followerRepository) {
        this.followerRepository = followerRepository;
    }

    public Boolean checkIfFollowed(Long userId, Long targetId){

        List<Follower> table = followerRepository.findAll();
        for(Follower follower : table){
            if(follower.getUserId().equals(userId) & follower.getTargetId().equals(targetId))
                return true;
        }
        return false;
    }

    public void unFollow(String userId, String targetId){
        followerRepository.Unfollow(Long.valueOf(userId).longValue(), Long.valueOf(targetId).longValue());
    }

    public void Follow(String user_id, String target_id){

        Follower follower = new Follower(Long.valueOf(user_id).longValue(), Long.valueOf(target_id).longValue());
        followerRepository.save(follower);
    }

    //to do wyświetlania followersów
    // todo
    public List<Follower> getFollowing(Long id){ return followerRepository.findByUserId(id); }
    public List<Follower> getFollowers(Long id){ return followerRepository.findByTargetId(id); }

    public Long getFollowersCount(Long userId){
        return followerRepository.countByTargetId(userId);
    }
    public Long getFollowingCount(Long userId){
        return followerRepository.countByUserId(userId);
    }
}
