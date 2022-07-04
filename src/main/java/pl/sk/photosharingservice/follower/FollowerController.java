package pl.sk.photosharingservice.follower;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sk.photosharingservice.appUser.appUserService;
import pl.sk.photosharingservice.support.AuthUtil;
import pl.sk.photosharingservice.support.ValidationUtil;
import pl.sk.photosharingservice.support.language.Language;

import java.util.List;

import static pl.sk.photosharingservice.support.ResponseUtil.USER_DOES_NOT_EXISTS;

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
    public ResponseEntity<?> follow(@RequestParam Long targetId, @RequestHeader("language") String language, @RequestHeader("authorization") String token) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Language l = (Language) Class.forName("pl.sk.photosharingservice.support.language." + language).newInstance();
        String username = AuthUtil.getUsernameFromToken(token);

        if (!appUserService.getUserById(targetId).isPresent())
            return new ResponseEntity<>(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), USER_DOES_NOT_EXISTS.translate(l)), HttpStatus.CONFLICT);

        followerService.handleFollow(appUserService.getUserIdByUsername(username), Long.valueOf(targetId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/users/followers")
    public ResponseEntity<?> getFollowers(@RequestHeader("authorization") String token) throws JsonProcessingException {
        List<Follower> followers = followerService.getFollowers(appUserService.getUserIdByUsername(AuthUtil.getUsernameFromToken(token)));
        return new ResponseEntity<>(followers, HttpStatus.OK);
    }

    @GetMapping("/users/following")
    public ResponseEntity<?> getFollowing(@RequestHeader("authorization") String token) throws JsonProcessingException {
        List<Follower> following = followerService.getFollowing(appUserService.getUserIdByUsername(AuthUtil.getUsernameFromToken(token)));
        return new ResponseEntity<>(following, HttpStatus.OK);
    }
}
