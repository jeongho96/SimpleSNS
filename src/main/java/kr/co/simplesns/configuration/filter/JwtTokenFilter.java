package kr.co.simplesns.configuration.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.simplesns.model.User;
import kr.co.simplesns.service.UserService;
import kr.co.simplesns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String key;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // get header
        final String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")) {
            log.error("Authorization header is incorrect. header is null or invalid");
            filterChain.doFilter(request, response);
            return;
        }

        try{
            final String token = header.split(" ")[1].trim();

            // TODO : check token is valid
            if(JwtTokenUtils.isExpired(token, key)){
                log.error("Expired JWT token");
                filterChain.doFilter(request, response);
                return;
            }

            // TODO : get username from token
            String userName = JwtTokenUtils.getUserName(token, key);
            // TODO : check the userName is valid
            User user = userService.loadUserByUserName(userName);


            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,null, user.getAuthorities()
            );

            authentication.setDetails(new WebAuthenticationDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch(RuntimeException e){
            log.error("Error occurs while invalidating. {}", e.toString());
            filterChain.doFilter(request, response);
            return;

        }

        filterChain.doFilter(request, response);
    }
}
