package pl.sk.photosharingservice.appUser;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import pl.sk.photosharingservice.follower.FollowerService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@DataJpaTest
@ActiveProfiles("test")
public class appUserServiceTest {

    @Autowired
    private appUserRepository userRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private FollowerService followerService;




    @Test
    void create() {
    }

    @Test
    void shouldReturnCorretList() {
        appUserService appUserService = new appUserService(userRepository,passwordEncoder, followerService);

        appUser user = new appUser("Smith", "1234", "email");
        appUser user2 = new appUser("Smith1", "1234", "email");

        appUserService.create(user);
        appUserService.create(user2);

        List<JSONObject> usr = new ArrayList<>();
        usr.add(user.toJson());
        usr.add(user2.toJson());

        Assertions.assertArrayEquals(usr.toArray(), appUserService.getUsersByPhrase("Smi").toArray());

    }

    @Test
    void shouldFindUsersEmail() {
        appUserService appUserService = new appUserService(userRepository,passwordEncoder, followerService);
        appUser user = new appUser("Smith", "1234", "email");
        appUserService.create(user);
        assertTrue(appUserService.findEmamil(user));


    }

    @Test
    void getUserIdByUsername() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void editProfile() {
    }

    @Test
    void setUserProfilePic() {
    }

    @Test
    void changePassword() {
    }

    @Test
    void findUser() {
    }

    @Test
    void findEmamil() {
    }

    @Test
    void loadUserByUsername() {
    }
}