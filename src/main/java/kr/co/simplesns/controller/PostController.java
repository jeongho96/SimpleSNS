package kr.co.simplesns.controller;

import kr.co.simplesns.controller.request.PostCommentRequest;
import kr.co.simplesns.controller.request.PostCreateRequest;
import kr.co.simplesns.controller.request.PostModifyRequest;
import kr.co.simplesns.controller.response.CommentResponse;
import kr.co.simplesns.controller.response.PostResponse;
import kr.co.simplesns.controller.response.Response;
import kr.co.simplesns.model.Post;
import kr.co.simplesns.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }


    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getBody(), authentication.getName());
        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Integer postId, @RequestBody PostModifyRequest request, Authentication authentication) {
        Post post = postService.modify(request.getTitle(), request.getBody(), authentication.getName(), postId);
        return Response.success(PostResponse.fromPost(post));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Integer postId, Authentication authentication) {
        postService.delete(authentication.getName(), postId);
        return Response.success();
    }

    @GetMapping
    public Response<Page<PostResponse>> list(Pageable pageable, Authentication authentication) {
        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> myPosts(Pageable pageable, Authentication authentication) {
        return Response.success(postService.my(authentication.getName(), pageable).map(PostResponse::fromPost));
    }


    // 좋아요 관련
    @PostMapping("/{postId}/likes")
    public Response<Void> like(@PathVariable Integer postId, Authentication authentication) {
        postService.like(postId, authentication.getName());
        return Response.success();
    }

    @GetMapping("/{postId}/likes")
    public Response<Integer> likeCount(@PathVariable Integer postId) {
        return Response.success(postService.likeCount(postId));
    }

    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> getComments(Pageable pageable, @PathVariable Integer postId) {
        return Response.success(postService.getComments(postId, pageable).map(CommentResponse::fromComment));
    }

    @PostMapping("/{postId}/comments")
    public Response<Void> comment(@PathVariable Integer postId, @RequestBody PostCommentRequest request, Authentication authentication) {
        postService.comment(postId, authentication.getName(), request.getComment());
        return Response.success();
    }


}
