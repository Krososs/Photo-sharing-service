package pl.sk.photosharingservice.service;


import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.sk.photosharingservice.model.User;
import pl.sk.photosharingservice.repository.UserRepository;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;




    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User Create(User user){
        user.setRole("ROLE_USER");
        user.setPassword( passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return user;
    }


    public JSONArray getUsersByPhrase(String phrase){
        List<User> all = userRepository.findAll();

        JSONArray users= new JSONArray();

        for(User user: all){
            if(user.getUsername().toLowerCase().contains(phrase.toLowerCase())){
                System.out.println(user.getUsername());
                System.out.println(phrase);

                JSONObject _user= new JSONObject();
                _user.put("username", user.getUsername());
                _user.put("id", user.getId());
                users.put(_user);
                //todo zdjęcie profilowe
            }
        }
        System.out.println(users);

        return users;
    }


    public User getUserByUsername(User user){
        return userRepository.findByUsername(user.getUsername());
    }

    public Optional<User> getUserById(Long id) {return userRepository.findById(id);}

    //check if username is taken
    public boolean findUser(User user){
        if(userRepository.findByUsername(user.getUsername())==null)
            return true;
        return false;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }


}
