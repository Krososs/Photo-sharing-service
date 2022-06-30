package pl.sk.photosharingservice.appUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
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
        this.imageService=imageService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("users/info")
    public ResponseEntity getUserInfo(@RequestHeader("authorization") String token) {

        JSONObject userInfo = new JSONObject();
        String username = AuthUtil.getUsernameFromToken(token);
        appUser appUser = appUserService.getUserByUsername(username);

        userInfo.put("username", appUser.getUsername());
        userInfo.put("id", appUser.getId());
        userInfo.put("profilePicture", appUser.getProfilePicture());

        return new ResponseEntity<>(userInfo.toMap(), HttpStatus.OK);
    }

    @GetMapping("/users/page")
    public ResponseEntity getUserPageData(@RequestParam(name = "ownerId")  String ownerId, @RequestHeader("authorization") String token) {
        return new ResponseEntity<>(appUserService.getUserPageData(ownerId,token).toMap(), HttpStatus.OK);
    }

    @GetMapping("/users/home")
    public ResponseEntity getUserHomePage(@RequestHeader("authorization") String token) {

        appUser appUser = appUserService.getUserByUsername(AuthUtil.getUsernameFromToken(token));
        JSONArray pageData = new JSONArray();

        List<Follower> following = followerService.getFollowing(appUser.getId()); //list of people that user is following
        List<Image> userHomePageData = new ArrayList<>(); //list of images

        for (Follower f: following){

            List<Image> images = imageService.getImagesById(f.getTargetId());
            for(Image image: images){
                userHomePageData.add(image);
            }

        }

        Collections.reverse(userHomePageData);

        for(Image i: userHomePageData){
            JSONObject post = new JSONObject();

            pl.sk.photosharingservice.appUser.appUser owner = appUserService.getUserById(i.getOwnerId()).get();

            boolean liked = imageService.checkIfLiked(appUser.getId(), i.getId());

            post.put("username", owner.getUsername());
            post.put("profilePhoto", owner.getProfilePicture()==null ? JSONObject.NULL : owner.getProfilePicture() );
            post.put("liked", liked);
            post.put("image", i);

            pageData.put(post);
        }
        return  ResponseEntity.ok(pageData.toList());
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> getUsersByPhrase(@RequestParam(name = "phrase") String phrase) {
        List<Object> _users = new JSONArray(appUserService.getUsersByPhrase(phrase)).toList();
        return ResponseEntity.ok(_users);
    }

    @GetMapping("/users/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String authHeader = request.getHeader(AUTHORIZATION);
        if(authHeader!=null && authHeader.startsWith("e ")){
            try{
                String refresh_token = authHeader.substring("e ".length());
                appUser appUser= appUserService.getUserByUsername(AuthUtil.getUsernameFromToken(refresh_token));

                Map<String, String> tokens = new HashMap<>();
                tokens.put("acces_token",AuthUtil.getAccesToken(appUser));
                tokens.put("refresh_token",refresh_token);

                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            }catch (Exception exception){

                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message",exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }

        }else{
            throw new RuntimeException("Refresh token is missing");

        }
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@ModelAttribute appUser appUser, @RequestHeader("language") String language) throws  ClassNotFoundException, InstantiationException, IllegalAccessException {

        Language l =  (Language)Class.forName("pl.sk.photosharingservice.support.language."+language).newInstance();
        JSONObject error = new JSONObject();

        if(!ValidationUtil.checkUsername(appUser.getUsername())){
            error.put("error", WRONG_USERNAME.translate(l));
            return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
        }
        if(!appUserService.findUser(appUser)){
            error.put("error", USERNAME_TAKEN.translate(l));
            return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
        }

        if (appUser.getPassword()==null || appUser.getPassword().length()==0){
            error.put("error", WRONG_PASSWORD.translate(l));
            return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
        }
        if(appUser.getPassword().length()< ValidationUtil.PASSWORD_MIN_LENGTH){
            error.put("error", PASSWORD_TOO_SHORT.translate(l));
            return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
        }
        if(!ValidationUtil.checkPassword(appUser.getPassword())){
            error.put("error", PASSWORD_TOO_WEAK.translate(l));
            return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
        }
        if(appUser.getPassword().contains(appUser.getUsername())){
            error.put("error", PASSWORD_CONTAINS_NAME.translate(l));
            return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
        }
        if(!ValidationUtil.checkEmail(appUser.getEmail())){
            error.put("error", WRONG_EMAIL.translate(l));
            return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
        }
        if(!appUserService.findEmamil(appUser)){
            error.put("error", EMAIL_TAKEN.translate(l));
            return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
        }

        appUser newAppUser = appUserService.create(appUser);

        return  ResponseEntity.ok(newAppUser);
    }

    @PostMapping("/users/profile/edit")
    public ResponseEntity<?> updateProfile(@ModelAttribute appUser appUser, @RequestHeader("language") String language, @RequestHeader("authorization") String token) throws  ClassNotFoundException, InstantiationException, IllegalAccessException {

        Language l =  (Language)Class.forName("pl.sk.photosharingservice.support.language."+language).newInstance();
        JSONObject error = new JSONObject();

        pl.sk.photosharingservice.appUser.appUser user = appUserService.getUserByUsername(AuthUtil.getUsernameFromToken(token));

        if(!user.getUsername().equals(appUser.getUsername())) {
            if (!appUserService.findUser(appUser)) {

                error.put("error", USERNAME_TAKEN.translate(l));
                return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
            }
        }

        if(!appUserService.findEmamil(appUser)){

            error.put("error", EMAIL_TAKEN.translate(l));
            return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
        }

        if(appUser.getDescription()!=null) {
            if (appUser.getDescription().length() > ValidationUtil.PROFILE_DESCRIPTION_MAX_LENGTH) {

                error.put("error", PROFILE_DESCRIPTION_TOO_LONG.translate(l));
                return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
            }
        }

        pl.sk.photosharingservice.appUser.appUser nUser = appUserService.editProfile(appUser, AuthUtil.getUsernameFromToken(token));
        Map<String, String> data = new HashMap<>();
        data.put("acces_token",AuthUtil.getAccesToken(nUser));
        data.put("refresh_token",AuthUtil.getRefreshToken(nUser));

        return  ResponseEntity.ok(data);
    }

    @PostMapping("users/profile/password/change")
    public ResponseEntity<?> changePassword( @RequestPart String oldPassword,@RequestPart String newPassword,@RequestPart String confirmPassword,  @RequestHeader("language") String language,@RequestHeader("authorization") String token) throws  ClassNotFoundException, InstantiationException, IllegalAccessException {

        String username = AuthUtil.getUsernameFromToken(token);
        appUser appUser = appUserService.getUserByUsername(username);

        Language l =  (Language)Class.forName("pl.sk.photosharingservice.support.language."+language).newInstance();
        JSONObject error = new JSONObject();

        if(!passwordEncoder.matches(oldPassword,appUser.getPassword())){
            error.put("error", WRONG_PASSWORD.translate(l));
            return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
        }
        if(!newPassword.equals(confirmPassword)){
            error.put("error", PASSWORDS_DO_NOT_MATCH.translate(l));
            return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
        }
        if(newPassword.length()< ValidationUtil.PASSWORD_MIN_LENGTH){
            error.put("error", PASSWORD_TOO_SHORT.translate(l));
            return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
        }
        if(!ValidationUtil.checkPassword(newPassword)){
            error.put("error", PASSWORD_TOO_WEAK.translate(l));
            return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
        }

        appUserService.changePassword(appUser, newPassword);

        return new ResponseEntity<>( HttpStatus.OK);
    }

    @PostMapping("/users/profile/profilePicture")
    public ResponseEntity<?> updateProfile(@RequestBody MultipartFile file, @RequestHeader("authorization") String token) throws IOException {
        appUserService.setUserProfilePic(file, AuthUtil.getUsernameFromToken(token));
        return new ResponseEntity<>( HttpStatus.OK);
    }

}
