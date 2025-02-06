package kr.co.simplesns.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import kr.co.simplesns.model.AlarmArgs;
import kr.co.simplesns.model.AlarmType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;



@Setter
@Getter
@Entity
@Table(name = "\"alarm\"", indexes = {
        @Index(name = "user_id_idx", columnList = "user_id")
})
@SQLDelete(sql = "UPDATE \"alarm\" SET removed_at = NOW() WHERE id=?")
@Where(clause = "deleted_at is NULL")
@NoArgsConstructor
public class AlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id = null;

    // 알람을 받은 사람
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 확장성을 고려해 알람 타입 지정
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    // com.vladmihalcea:hibernate-types 의존성 추가 필요
    // com.vladmihalcea:hibernate-types-57은 과거 hibernate 5.x
    // com.vladmihalcea:hibernate-types-60은 최신 hibernate 6.x
    // 호환 고려해서 사용.
    // hibernate는 기본적으로 jsonb 타입을 제공하지 않음.
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private AlarmArgs alarmArgs;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;


    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static AlarmEntity of(UserEntity user, AlarmType alarmType, AlarmArgs alarmArgs) {
        AlarmEntity entity = new AlarmEntity();
        entity.setAlarmArgs(alarmArgs);
        entity.setAlarmType(alarmType);
        entity.setUser(user);
        return entity;
    }
}