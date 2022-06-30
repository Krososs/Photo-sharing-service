package pl.sk.photosharingservice.appUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.sk.photosharingservice.follower.Follower;
import pl.sk.photosharingservice.image.Image;
import pl.sk.photosharingservice.follower.FollowerService;
import pl.sk.photosharingservice.image.ImageService;
import pl.sk.photosharingservice.support.*;
import pl.sk.photosharingservice.support.language.Language;

import java.io.IOException;
import java.util.*;

import static pl.sk.photosharingservice.support.ResponseUtil.*;


@RestController
public class appUserController {

    private final appUserService appUserService;
    private final ImageService imageService;
    private final FollowerService followerService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public appUserController(appUserService appUserService, FollowerService followerService, ImageService imageService, PasswordEncoder passwordEncoder) {
        this.appUserService = appUserService;
        this.followerService = followerService;
        this.imageService = imageService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("users/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("authorization") String token) {

        ObjectNode userInfo = new ObjectMapper().createObjectNode();
        String username = AuthUtil.getUsernameFromToken(token);
        appUser appUser = appUserService.getUserByUsername(username);

        userInfo.put("username", appUser.getUsername());
        userInfo.put("id", appUser.getId());
        userInfo.put("profilePicture", appUser.getProfilePicture());

        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @GetMapping("/users/page")
    public ResponseEntity<?> getUserPageData(@RequestParam(name = "ownerId") String ownerId, @RequestHeader("authorization") String token) {
        return new ResponseEntity(appUserService.getUserPageData(ownerId, token), HttpStatus.OK);
    }

    @GetMapping("/users/home")
    public ResponseEntity<?> getUserHomePage(@RequestHeader("authorization") String token) {

        appUser appUser = appUserService.getUserByUsername(AuthUtil.getUsernameFromToken(token));

        ObjectNode pageInfo = new ObjectMapper().createObjectNode();
        ArrayNode posts = pageInfo.putArray("posts");

        List<Follower> following = followerService.getFollowing(appUser.getId()); //list of people that user is following
        List<Image> userHomePageData = new ArrayList<>(); //list of images

        for (Follower f : following) {
            userHomePageData.addAll(imageService.getImagesById(f.getTargetId()));
        }
        Collections.reverse(userHomePageData);
        for (Image i : userHomePageData) {

            ObjectNode _post = new ObjectMapper().createObjectNode();

            appUser owner = appUserService.getUserById(i.getOwnerId()).get();

            boolean liked = imageService.checkIfLiked(appUser.getId(), i.getId());
            _post.put("username", owner.getUsername());
            _post.put("profilePhoto", owner.getProfilePicture() == null ? null : owner.getProfilePicture());
            _post.put("liked", liked);
            _post.put("image", i.toJson());

            posts.add(_post);
        }
        return ResponseEntity.ok(pageInfo);
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> getUsersByPhrase(@RequestParam(name = "phrase") String phrase) {
        List<ObjectNode> _users = appUserService.getUsersByPhrase(phrase);
        return new ResponseEntity(_users, HttpStatus.OK);
    }

    @GetMapping("users/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("authorization") String refreshToken) {

        if (refreshToken == null || refreshToken.length() == 0)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        try {
            String username = AuthUtil.getUsernameFromToken(refreshToken);
            appUser user = appUserService.getUserByUsername(username);

            Map<String, String> data = new HashMap<>();
            data.put("acces_token", AuthUtil.getAccesToken(user));
            data.put("refresh_token", AuthUtil.getRefreshToken(user));

            return new ResponseEntity(data, HttpStatus.OK);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.FORBIDDEN.value(), exception.getMessage()), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@ModelAttribute appUser appUser, @RequestHeader("language") String language) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Language l = (Language) Class.forName("pl.sk.photosharingservice.support.language." + language).newInstance();

        if (!ValidationUtil.checkUsername(appUser.getUsername()))
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), WRONG_USERNAME.translate(l)), HttpStatus.CONFLICT);
        if (!appUserService.findUser(appUser))
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), USERNAME_TAKEN.translate(l)), HttpStatus.CONFLICT);
        if (appUser.getPassword() == null || appUser.getPassword().length() == 0)
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), WRONG_PASSWORD.translate(l)), HttpStatus.CONFLICT);
        if (appUser.getPassword().length() < ValidationUtil.PASSWORD_MIN_LENGTH)
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), PASSWORD_TOO_SHORT.translate(l)), HttpStatus.CONFLICT);
        if (!ValidationUtil.checkPassword(appUser.getPassword()))
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), PASSWORD_TOO_WEAK.translate(l)), HttpStatus.CONFLICT);
        if (appUser.getPassword().contains(appUser.getUsername()))
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), PASSWORD_CONTAINS_NAME.translate(l)), HttpStatus.CONFLICT);
        if (!ValidationUtil.checkEmail(appUser.getEmail()))
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), WRONG_EMAIL.translate(l)), HttpStatus.CONFLICT);
        if (!appUserService.findEmamil(appUser))
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), EMAIL_TAKEN.translate(l)), HttpStatus.CONFLICT);

        appUserService.create(appUser);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/users/profile/edit")
    public ResponseEntity<?> updateProfile(@ModelAttribute appUser appUser, @RequestHeader("language") String language, @RequestHeader("authorization") String token) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Language l = (Language) Class.forName("pl.sk.photosharingservice.support.language." + language).newInstance();
        appUser user = appUserService.getUserByUsername(AuthUtil.getUsernameFromToken(token));

        if (!user.getUsername().equals(appUser.getUsername())) {
            if (!appUserService.findUser(appUser))
                return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), USERNAME_TAKEN.translate(l)), HttpStatus.CONFLICT);
        }

        if (!appUserService.findEmamil(appUser))
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), EMAIL_TAKEN.translate(l)), HttpStatus.CONFLICT);
        if (appUser.getDescription() != null) {
            if (appUser.getDescription().length() > ValidationUtil.PROFILE_DESCRIPTION_MAX_LENGTH)
                return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), PROFILE_DESCRIPTION_TOO_LONG.translate(l)), HttpStatus.CONFLICT);
        }

        appUser nUser = appUserService.editProfile(appUser, AuthUtil.getUsernameFromToken(token));
        Map<String, String> data = new HashMap<>();
        data.put("acces_token", AuthUtil.getAccesToken(nUser));
        data.put("refresh_token", AuthUtil.getRefreshToken(nUser));

        return new ResponseEntity(data, HttpStatus.OK);
    }

    @PostMapping("users/profile/password/change")
    public ResponseEntity<?> changePassword(@RequestPart String oldPassword, @RequestPart String newPassword, @RequestPart String confirmPassword, @RequestHeader("language") String language, @RequestHeader("authorization") String token) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        String username = AuthUtil.getUsernameFromToken(token);
        appUser appUser = appUserService.getUserByUsername(username);
        Language l = (Language) Class.forName("pl.sk.photosharingservice.support.language." + language).newInstance();

        if (!passwordEncoder.matches(oldPassword, appUser.getPassword()))
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), WRONG_PASSWORD.translate(l)), HttpStatus.CONFLICT);
        if (!newPassword.equals(confirmPassword))
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), PASSWORDS_DO_NOT_MATCH.translate(l)), HttpStatus.CONFLICT);
        if (newPassword.length() < ValidationUtil.PASSWORD_MIN_LENGTH)
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), PASSWORD_TOO_SHORT.translate(l)), HttpStatus.CONFLICT);
        if (!ValidationUtil.checkPassword(newPassword))
            return new ResponseEntity(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), PASSWORD_TOO_WEAK.translate(l)), HttpStatus.CONFLICT);

        appUserService.changePassword(appUser, newPassword);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/users/profile/profilePicture")
    public ResponseEntity<?> updateProfile(@RequestBody MultipartFile file, @RequestHeader("authorization") String token) throws IOException {
        appUserService.setUserProfilePic(file, AuthUtil.getUsernameFromToken(token));
        return new ResponseEntity(HttpStatus.OK);
    }

}
