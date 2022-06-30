package pl.sk.photosharingservice.appUser;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.sk.photosharingservice.follower.FollowerService;
import pl.sk.photosharingservice.support.AuthUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class appUserService implements UserDetailsService {

    private final appUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final FollowerService followerService;

    @Autowired
    public appUserService(appUserRepository appUserRepository, PasswordEncoder passwordEncoder, FollowerService followerService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.followerService = followerService;
    }

    public appUser create(appUser appUser){
        appUser.setRole("ROLE_USER");
        appUser.setPassword( passwordEncoder.encode(appUser.getPassword()));
        appUser.setJoiningDate(new Date());
        appUserRepository.save(appUser);
        return appUser;
    }

    public JSONObject getUserPageData(String ownerId, String token){

        JSONObject pageData = new JSONObject();
        String username = AuthUtil.getUsernameFromToken(token);

        appUser appUser = getUserById(Long.valueOf(ownerId)).get();

        Boolean followed = followerService.checkIfFollowed(getUserIdByUsername(username),Long.valueOf(ownerId));
        Boolean myPage= username.equals(appUser.getUsername());

        pageData.put("myPage", myPage);
        pageData.put("username", appUser.getUsername());
        pageData.put("email", appUser.getEmail());
        pageData.put("posts", appUser.getPosts());
        pageData.put("followed", followed);
        pageData.put("followers", appUser.getFollowers());
        pageData.put("following", appUser.getFollowing());
        pageData.put("description", appUser.getDescription());
        pageData.put("profilePicture", appUser.getProfilePicture());
        pageData.put("joiningDate", appUser.getJoiningDate());

        return pageData;
    }

    public List<?> getUsersByPhrase(String phrase){

        return appUserRepository.findByUsernameContaining(phrase)
                .stream()
                .map(appUser -> appUser.toJson())
                .collect(Collectors.toList());
    }

    public appUser getUserByUsername(String username){
        return appUserRepository.findByUsername(username);
    }
    
    public Long getUserIdByUsername(String username){
        return  appUserRepository.findByUsername(username).getId();
    }

    public Optional<appUser> getUserById(Long id) {return appUserRepository.findById(id);}

    public appUser editProfile(appUser appUser, String username){

        //current user
        appUser user = appUserRepository.findByUsername(username);

        if(appUser.getUsername()!=null) {
            user.setUsername(appUser.getUsername());
        }
        if(appUser.getDescription()!=null) {
            user.setDescription(appUser.getDescription());
        }
        if(appUser.getEmail()!=null) {
            user.setEmail(appUser.getEmail());
        }
        if(appUser.getPassword()!=null) {
            user.setPassword(passwordEncoder.encode(appUser.getPassword()));
        }
        appUserRepository.save(user);
        return user;
    }


    public void setUserProfilePic(MultipartFile file, String username) throws IOException {
        appUser appUser = appUserRepository.findByUsername(username);
        appUser.setProfilePicture(Base64.getEncoder().encodeToString(file.getBytes()));
        appUserRepository.save(appUser);
    }

    public void changePassword(appUser appUser, String password){
        appUser.setPassword(passwordEncoder.encode(password));
        appUserRepository.save(appUser);
    }

    //check if username is taken
    public boolean findUser(appUser appUser){
        if(appUser.getUsername()==null|| appUserRepository.findByUsername(appUser.getUsername())==null)
            return true;
        return false;
    }

    //check if email is taken
    public boolean findEmamil(appUser appUser){
        if(appUser.getEmail()==null || appUserRepository.findByEmail(appUser.getEmail())==null)
            return true;
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        appUser appUser = appUserRepository.findByUsername(username);
        if(appUser == null){
            throw new UsernameNotFoundException("User not found");
        }else{
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(appUser.getRole()));
            return new org.springframework.security.core.userdetails.User(appUser.getUsername(), appUser.getPassword(),authorities);
        }
    }


}
