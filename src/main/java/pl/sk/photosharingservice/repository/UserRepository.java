package pl.sk.photosharingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sk.photosharingservice.model.AppUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByUsername(String username);
    List<AppUser> findByPassword(String password);
    Optional<AppUser> findById(Long id);

}
