package pl.sk.photosharingservice.follower;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {

     @Modifying
     @Transactional
     @Query(value="DELETE FROM Follower b WHERE b.userId = :userId AND b.targetId = :targetId")
     void Unfollow(@Param("userId") Long userId, @Param("targetId") Long targetId);

     @Transactional
     @Query(value="SELECT f FROM Follower f WHERE f.userId = :userId AND f.targetId = :targetId")
     Optional<Follower> checkIfFollowed(@Param("userId") Long userId, @Param("targetId")Long targetId);

     List<Follower> findByTargetId(Long id);
     List<Follower> findByUserId(Long id);

}
