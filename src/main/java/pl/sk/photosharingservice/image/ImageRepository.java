package pl.sk.photosharingservice.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findById(Long id);
    @Transactional
    @Query(value="SELECT l FROM Like l WHERE l.userId = :userId AND l.imageId = :imageId")
    Optional<Image> checkIfLiked(@Param("userId") Long userId,@Param("imageId")Long imageId);

    @Modifying
    @Transactional
    @Query(value="DELETE FROM Image i WHERE i.id = :imageId")
    void deleteImageById(@Param("imageId") Long imageId);

    void deleteById(Long id);
    List<Image> findByOwnerId(Long id);
}
