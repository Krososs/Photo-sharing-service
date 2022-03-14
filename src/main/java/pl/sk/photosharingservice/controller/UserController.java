package pl.sk.photosharingservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.sk.photosharingservice.model.AppUser;
import pl.sk.photosharingservice.repository.UserRepository;
import pl.sk.photosharingservice.service.FollowerService;
import pl.sk.photosharingservice.service.ImageService;
import pl.sk.photosharingservice.service.UserService;

import java.util.List;


@RestController
public class UserController {

    @Autowired
    ObjectMapper objectMapper;

    private final UserService userService;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;
    private final FollowerService followerService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, UserRepository userRepository, FollowerService followerService, ImageService imageService) {
        this.userService = userService;
        this.followerService = followerService;
        this.passwordEncoder = passwordEncoder;
        this.imageService=imageService;

    }

    @GetMapping("/users/page")
    public ResponseEntity getUserData(@RequestParam(name = "userId")  String userId, @RequestParam(name = "ownerId")  String ownerId) throws JsonProcessingException {

        System.out.println("Zapytanie użytkownika "+userId+" o użytkownika " + ownerId);
        JSONObject pageData = new JSONObject();

        AppUser appUser = userService.getUserById(Long.valueOf(ownerId).longValue()).get();
        Boolean followed = followerService.checkIfFollowed(Long.valueOf(userId).longValue(),Long.valueOf(ownerId).longValue());
        Long posts = imageService.getUserPhotosCount(Long.valueOf(ownerId).longValue());

        Long followers = followerService.getFollowersCount(Long.valueOf(ownerId).longValue());
        Long following = followerService.getFollowingCount(Long.valueOf(ownerId).longValue());

        pageData.put("username", appUser.getUsername());
        pageData.put("posts", posts);
        pageData.put("followed", followed);
        pageData.put("followers", followers);
        pageData.put("following", following);
        pageData.put("description", appUser.getDescription());


        return new ResponseEntity<>(pageData.toMap(), HttpStatus.OK);

    }


    @GetMapping("/users/search")
    public ResponseEntity<?> getUsersByPhrase(@RequestParam(name = "phrase") String phrase) throws JsonProcessingException {
        //JSONArray users= new JSONArray(userService.getUsersByPhrase(phrase));
        List<Object> _users = new JSONArray(userService.getUsersByPhrase(phrase)).toList();
        //System.out.println(users);
        System.out.println(_users);
        return ResponseEntity.ok(_users);

    }


    @PostMapping("/users/register")
    public ResponseEntity registerUser(@RequestBody AppUser appUser){

        if(!userService.findUser(appUser)){
            return ResponseEntity.ok("Użytkownik o podanym nicku już istnieje");
        }

        System.out.println("USER REGISTERED");
        System.out.println(appUser);
        AppUser newAppUser = userService.Create(appUser);
        return  ResponseEntity.ok(newAppUser);
    }

    @PostMapping("/users/update/description")
    public ResponseEntity updateDescription(@RequestBody String description, @RequestParam(name = "userId")  String userId){
        System.out.println(description);

        return  ResponseEntity.ok("k");
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody User user) throws JsonProcessingException {
//
//
//        if(userService.findUser(user)){
//            return new ResponseEntity<>("Niepoprawny login",HttpStatus.UNAUTHORIZED);
//        }
//
//        User dbUser = userService.getUserByUsername(user);
//
//        if(!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())){
//            System.out.println("Niepoprawne hasło");
//            System.out.println(user.getPassword());
//            System.out.println(dbUser.getPassword());
//            return new ResponseEntity<>("Incorrect password",HttpStatus.UNAUTHORIZED);
//        }
//        return new ResponseEntity<>(dbUser.getId().toString(),HttpStatus.OK);
//
//    }






}
