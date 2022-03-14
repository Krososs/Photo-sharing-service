package pl.sk.photosharingservice.service;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.sk.photosharingservice.model.AppUser;
import pl.sk.photosharingservice.repository.UserRepository;

import java.util.*;

@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;




    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser Create(AppUser appUser){
        appUser.setRole("ROLE_USER");
        appUser.setPassword( passwordEncoder.encode(appUser.getPassword()));
        AppUser savedAppUser = userRepository.save(appUser);
        return appUser;
    }


    public JSONArray getUsersByPhrase(String phrase){
        List<AppUser> all = userRepository.findAll();

        JSONArray users= new JSONArray();

        for(AppUser appUser : all){
            if(appUser.getUsername().toLowerCase().contains(phrase.toLowerCase())){
                System.out.println(appUser.getUsername());
                System.out.println(phrase);

                JSONObject _user= new JSONObject();
                _user.put("username", appUser.getUsername());
                _user.put("id", appUser.getId());
                users.put(_user);
                //todo zdjęcie profilowe
            }
        }
        System.out.println(users);

        return users;
    }


    public AppUser getUserByUsername(AppUser user){
        return userRepository.findByUsername(user.getUsername());
    }

    public Optional<AppUser> getUserById(Long id) {return userRepository.findById(id);}

    //check if username is taken
    public boolean findUser(AppUser appUser){
        if(userRepository.findByUsername(appUser.getUsername())==null)
            return true;
        return false;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username);
        if(appUser == null){
            System.out.println("User not found");
            throw new UsernameNotFoundException("User not found");
        }else{
            System.out.println("User found");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        //todo user roles as an array

        authorities.add(new SimpleGrantedAuthority(appUser.getRole()));

        return new org.springframework.security.core.userdetails.User(appUser.getUsername(), appUser.getPassword(),authorities);
    }


}
