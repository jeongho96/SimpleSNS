package kr.co.simplesns.service;

import kr.co.simplesns.exception.ErrorCode;
import kr.co.simplesns.exception.SnsApplicationException;
import kr.co.simplesns.model.Post;
import kr.co.simplesns.model.entity.PostEntity;
import kr.co.simplesns.model.entity.UserEntity;
import kr.co.simplesns.repository.PostEntityRepository;
import kr.co.simplesns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    public List<Post> findAll() {
        List<PostEntity> postEntities = postEntityRepository.findAll();

        return postEntities.stream()
                .map(Post::fromEntity)
                .collect(Collectors.toList());
    }

    public Post getPostById(Integer postId) {
        // 게시물 조회
        PostEntity postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("Post with id %d not found", postId)));

        // PostEntity를 Post로 변환
        return Post.fromEntity(postEntity);
    }

    // entity mapping
    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName, Pageable pageable) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()
                -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));

        return postEntityRepository.findAllByUserId(userEntity.getId(), pageable).map(Post::fromEntity);
    }

    @Transactional
    public void create(String title, String body, String userName){
        // user find
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()
        -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));
        PostEntity saved = postEntityRepository.save(PostEntity.of(title, body, userEntity));

    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId){
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()
                -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));


        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not found", postId)));

        // post permission
        if(postEntity.getUser() != userEntity){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }


        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));

    }

    @Transactional
    public void delete(String userName, Integer postId){

        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()
                -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));


        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not found", postId)));

        // post permission
        if(postEntity.getUser() != userEntity){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntityRepository.delete(postEntity);

    }
}
