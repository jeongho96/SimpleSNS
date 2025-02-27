package kr.co.simplesns.service;


import kr.co.simplesns.exception.ErrorCode;
import kr.co.simplesns.exception.SnsApplicationException;
import kr.co.simplesns.model.Alarm;
import kr.co.simplesns.model.User;
import kr.co.simplesns.model.entity.UserEntity;
import kr.co.simplesns.repository.AlarmEntityRepository;
import kr.co.simplesns.repository.UserEntityRepository;
import kr.co.simplesns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder encoder;
    private final AlarmEntityRepository alarmEntityRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long tokenExpiredTimeMs;

    public User loadUserByUserName(String userName){
        return userEntityRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("User with name %s not found", userName))
        );
    }

    @Transactional
    public User join(String userName, String password) {
        // 회원가입하려는 userName으로 회원가입된 user가 있는지
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
                });
        // 회원가입 진행 = user를 등록

        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));
        
        return User.fromEntity(userEntity);
    }

    // TODO : implement
    public String login(String userName, String password) {
        // 회원가입 여부 확인
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException(
                ErrorCode.USER_NOT_FOUND,String.format("%s is not found", userName)
        ));
        // 비밀번호 체크
       if(!encoder.matches(password, userEntity.getPassword())) {
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
       }

        // 토큰 생성
        String token = JwtTokenUtils.generateToken(userName, secretKey, tokenExpiredTimeMs);

        return token;
    }

    public Page<Alarm> alarmList(String userName, Pageable pageable) {
        // 회원가입 여부 확인
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException(
                ErrorCode.USER_NOT_FOUND,String.format("%s is not found", userName)
        ));

        return alarmEntityRepository.findAllByUser(userEntity, pageable).map(Alarm::fromEntity);



    }


}
