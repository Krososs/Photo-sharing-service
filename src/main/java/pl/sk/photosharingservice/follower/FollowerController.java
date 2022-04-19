package pl.sk.photosharingservice.follower;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sk.photosharingservice.appUser.appUserService;
import pl.sk.photosharingservice.support.AuthUtil;

import java.util.List;

@RestController
public class FollowerController {

    private final FollowerService followerService;
    private final appUserService appUserService;

    @Autowired
    public FollowerController(FollowerService followerService, appUserService appUserService) {
        this.followerService = followerService;
        this.appUserService = appUserService;
    }

    @PostMapping("/users/follow")
    public ResponseEntity<?> follow(@RequestParam(name = "targetId") String targetId,@RequestHeader("authorization") String token) {
        String username = AuthUtil.getUsernameFromToken(token);
        followerService.handleFollow(appUserService.getUserIdByUsername(username),Long.valueOf(targetId));
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @GetMapping("/users/followers")
    public ResponseEntity<?> getFollowers(@RequestHeader("authorization") String token) throws JsonProcessingException {

        List<Follower> followers = followerService.getFollowers(appUserService.getUserIdByUsername(AuthUtil.getUsernameFromToken(token)));
        return ResponseEntity.ok(followers.toArray());

    }
    @GetMapping("/users/following")
    public ResponseEntity<?> getFollowing(@RequestHeader("authorization") String token) throws JsonProcessingException {

        List<Follower> followers = followerService.getFollowing(appUserService.getUserIdByUsername(AuthUtil.getUsernameFromToken(token)));
        return ResponseEntity.ok(followers.toArray());
    }

}
