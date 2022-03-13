package pl.sk.photosharingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sk.photosharingservice.model.Image;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Long countByOwnerId(Long id);
    List<Image> findByOwnerId(Long id);
}
