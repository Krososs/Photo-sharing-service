package pl.sk.photosharingservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.sk.photosharingservice.model.Follower;
import pl.sk.photosharingservice.service.FollowerService;

import java.util.List;

@RestController
public class FollowerController {

    @Autowired
    ObjectMapper objectMapper;

    private final FollowerService followerService;

    @Autowired
    public FollowerController(FollowerService followerService) {
        this.followerService = followerService;
    }

    @PostMapping("/users/follow")
    public ResponseEntity follow(@RequestParam(name = "userId") String userId, String targetId) {

        System.out.println(userId);
        System.out.println(targetId);
        followerService.Follow(userId, targetId);
        return new ResponseEntity<>("Followed", HttpStatus.OK);

    }

    @PostMapping("/users/unfollow")
    public ResponseEntity unfollow(@RequestParam(name = "userId") String userId, String targetId) {

        followerService.unFollow(userId, targetId);
        return new ResponseEntity<>("Unfollowed", HttpStatus.OK);

    }

    //my followers
    @GetMapping("/users/followers")
    public ResponseEntity getFollowers(String userId) throws JsonProcessingException {

        List<Follower> followers = followerService.getFollowers(Long.valueOf(userId).longValue());
        return ResponseEntity.ok(objectMapper.writeValueAsString(followers));

    }

    //people I follow
    @GetMapping("/users/following")
    public ResponseEntity getFollowing(String userId) throws JsonProcessingException {

        List<Follower> followers = followerService.getFollowing(Long.valueOf(userId).longValue());
        return ResponseEntity.ok(objectMapper.writeValueAsString(followers));


    }

}
