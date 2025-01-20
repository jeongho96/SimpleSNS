package kr.co.simplesns.service;


import kr.co.simplesns.exception.ErrorCode;
import kr.co.simplesns.exception.SnsApplicationException;
import kr.co.simplesns.fixture.PostEntityFixture;
import kr.co.simplesns.fixture.UserEntityFixture;
import kr.co.simplesns.model.entity.PostEntity;
import kr.co.simplesns.model.entity.UserEntity;
import kr.co.simplesns.repository.PostEntityRepository;
import kr.co.simplesns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockitoBean
    private PostEntityRepository postEntityRepository;
    @MockitoBean
    private UserEntityRepository userEntityRepository;


    @Test
    void 포스트작성이_성공한경우(){
        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        Assertions.assertDoesNotThrow(() -> postService.create(title, body, userName));

    }

    @Test
    void 포스트작성시_요청한유저가_존재하지_않는_경우(){
        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.create(title, body, userName));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());


    }


    @Test
    void 포스트수정이_성공한경우(){
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        // mocking
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);


        Assertions.assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));

    }

    @Test
    void 포스트수정시_포스트가_존재하지않는_경우(){
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        // mocking
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());


        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());

    }

    @Test
    void 포스트수정시_권한이_없는_경우(){
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        // mocking
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();
        UserEntity writer = UserEntityFixture.get("username1","password", 2);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));


        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());

    }

    @Test
    void 포스트삭제가_성공한경우(){
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        // mocking
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));



        Assertions.assertDoesNotThrow(() -> postService.delete(userName, 1));

    }

    @Test
    void 포스트삭제시_포스트가_존재하지않는_경우(){

        String userName = "userName";
        Integer postId = 1;

        // mocking
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());


        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.delete(userName, 1));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());

    }

    @Test
    void 포스트삭제시_권한이_없는_경우(){
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        // mocking
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity writer = UserEntityFixture.get("username1","password", 2);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));


        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.delete(userName, 1));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());

    }



    @Test
    void 포스트목록요청이_성공한경우() {
        Pageable pageable = mock(Pageable.class);
        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());
        Assertions.assertDoesNotThrow(() -> postService.list(pageable));
    }

    @Test
    void 내포스트목록요청이_성공한경우() {

        Pageable pageable = mock(Pageable.class);
        UserEntity userEntity = mock(UserEntity.class);
        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findAllByUserId(userEntity.getId(),pageable)).thenReturn(Page.empty());
        Assertions.assertDoesNotThrow(() -> postService.my("",pageable));
    }

}
