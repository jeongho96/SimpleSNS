package kr.co.simplesns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "User name is duplicated"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid Password"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Token"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post not found"),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "Invalid Permission"),
    ALREADY_LIKED(HttpStatus.CONFLICT, "Already Liked"),
    ;


    private HttpStatus status;
    private String message;
}
