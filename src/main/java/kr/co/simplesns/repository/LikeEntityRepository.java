package kr.co.simplesns.repository;

import kr.co.simplesns.model.entity.LikeEntity;
import kr.co.simplesns.model.entity.PostEntity;
import kr.co.simplesns.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {

    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);

    @Query(value = "SELECT COUNT(*) FROM LikeEntity entity WHERE entity.post =:post")
    Integer countByPost(@Param("post") PostEntity post);

    // 이걸로 가져와서 좋아요 개수를 카운트하면 엔티티를 전부 가져오는 비효율이 있음. 위 코드 추천
    List<LikeEntity> findAllByPost(PostEntity post);
}
