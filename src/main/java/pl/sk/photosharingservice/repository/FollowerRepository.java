package pl.sk.photosharingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.sk.photosharingservice.model.Follower;

import java.util.List;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {

     @Modifying
     @Transactional
     @Query(value="DELETE FROM Follower b WHERE b.userId = :userId AND b.targetId = :targetId")
     void Unfollow(@Param("userId") Long userId, @Param("targetId") Long targetId);
     List<Follower> findByTargetId(Long id);
     List<Follower> findByUserId(Long id);
     Long countByTargetId(Long id);
     Long countByUserId(Long id);
}
