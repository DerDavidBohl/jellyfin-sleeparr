package org.davidbohl.jellyfin_sleeparr.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.JellyfinApiConsumer;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.models.JellyfinUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JellyfinApiConsumer jellyfinApiConsumer;

    public JwtAuthFilter(JwtService jwtService, JellyfinApiConsumer jellyfinApiConsumer) {
        this.jwtService = jwtService;
        this.jellyfinApiConsumer = jellyfinApiConsumer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userId = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            userId = jwtService.getUserId(token);
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            JellyfinUser jellyfinUser = jellyfinApiConsumer.getUserById(userId);

            List<GrantedAuthority> authorities = new java.util.ArrayList<>(
                    List.of(new SimpleGrantedAuthority("USER"))
            );

            if (jellyfinUser.getPolicy().isAdministrator())
                authorities.add(new SimpleGrantedAuthority("ADMIN"));

            UserDetails userDetails = new SleeparrUserDetails(jellyfinUser.getId(), jellyfinUser.getName(), authorities);

            if (jwtService.validate(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}