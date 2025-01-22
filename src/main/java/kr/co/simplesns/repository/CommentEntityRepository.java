package kr.co.simplesns.repository;


import kr.co.simplesns.model.entity.CommentEntity;
import kr.co.simplesns.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CommentEntityRepository extends JpaRepository<CommentEntity, Integer> {


    Page<CommentEntity> findAllByPost(PostEntity post, Pageable pageable);

}
