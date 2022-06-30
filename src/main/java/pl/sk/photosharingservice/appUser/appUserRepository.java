package pl.sk.photosharingservice.appUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface appUserRepository extends JpaRepository<appUser, Long> {

    appUser findByUsername(String username);
    appUser findByEmail(String email);
    Optional<appUser> findById(Long id);
    List<appUser> findByUsernameContaining(String phrase);

}
