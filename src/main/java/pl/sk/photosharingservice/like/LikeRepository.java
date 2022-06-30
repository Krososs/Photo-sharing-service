package pl.sk.photosharingservice.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
        @Modifying
        @Transactional
        @Query(value="DELETE FROM Like l WHERE l.userId = :userId AND l.imageId = :imageId")
        void Unlike(@Param("userId") Long userId, @Param("imageId")Long imageId);
        List<Like> findByImageId(Long imageId);
}
