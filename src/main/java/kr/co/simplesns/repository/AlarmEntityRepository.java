package kr.co.simplesns.repository;

import kr.co.simplesns.model.entity.AlarmEntity;
import kr.co.simplesns.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;




public interface AlarmEntityRepository extends JpaRepository<AlarmEntity, Long> {
    Page<AlarmEntity> findAllByUser(UserEntity user, Pageable pageable);
}
