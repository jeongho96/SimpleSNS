package kr.co.simplesns.service;

import kr.co.simplesns.exception.ErrorCode;
import kr.co.simplesns.exception.SnsApplicationException;
import kr.co.simplesns.model.Comment;
import kr.co.simplesns.model.Post;
import kr.co.simplesns.model.entity.CommentEntity;
import kr.co.simplesns.model.entity.LikeEntity;
import kr.co.simplesns.model.entity.PostEntity;
import kr.co.simplesns.model.entity.UserEntity;
import kr.co.simplesns.repository.CommentEntityRepository;
import kr.co.simplesns.repository.LikeEntityRepository;
import kr.co.simplesns.repository.PostEntityRepository;
import kr.co.simplesns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;

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
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;

    public List<Post> findAll() {
        List<PostEntity> postEntities = postEntityRepository.findAll();

        return postEntities.stream()
                .map(Post::fromEntity)
                .collect(Collectors.toList());
    }

    public Post getPostById(Integer postId) {
        // 게시물 조회
        PostEntity postEntity = getPostEntityOrException(postId);

        // PostEntity를 Post로 변환
        return Post.fromEntity(postEntity);
    }

    // entity mapping
    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }



    public Page<Post> my(String userName, Pageable pageable) {
        UserEntity userEntity = getUserEntityOrException(userName);

        return postEntityRepository.findAllByUserId(userEntity.getId(), pageable).map(Post::fromEntity);
    }

    @Transactional
    public void create(String title, String body, String userName){
        // user find
        UserEntity userEntity = getUserEntityOrException(userName);
        PostEntity saved = postEntityRepository.save(PostEntity.of(title, body, userEntity));

    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId){
        UserEntity userEntity = getUserEntityOrException(userName);

        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

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

        UserEntity userEntity = getUserEntityOrException(userName);
        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

        // post permission
        if(postEntity.getUser() != userEntity){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntityRepository.delete(postEntity);

    }

    @Transactional
    public void like(Integer postId, String userName){
        UserEntity userEntity = getUserEntityOrException(userName);
        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);


        // check liked -> throw
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s already like the post %s", userName, postId));
        });

        likeEntityRepository.save(LikeEntity.of(postEntity, userEntity));
    }


    public Integer likeCount(Integer postId){
        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);
        // count like
        return likeEntityRepository.countByPost(postEntity);

    }

    @Transactional
    public void comment(Integer postId, String userName, String comment) {
        PostEntity postEntity = getPostEntityOrException(postId);
        UserEntity userEntity = getUserEntityOrException(userName);

        commentEntityRepository.save(CommentEntity.of(comment, postEntity, userEntity));

        // create alarm
        // notificationService.send(AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(userEntity.getId(), postId), postEntity.getUser());
//        alarmProducer.send(new AlarmEvent(AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(userEntity.getId(), postId), postEntity.getUser().getId()));
    }

    public Page<Comment> getComments(Integer postId, Pageable pageable) {
        PostEntity postEntity = getPostEntityOrException(postId);
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
    }


    // User exist
    private PostEntity getPostEntityOrException(Integer postId){
        return postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("Post with id %d not found", postId)));
    }

    // Post exist
    private UserEntity getUserEntityOrException(String userName){
        return userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));
    }

}
