package org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1;

import lombok.RequiredArgsConstructor;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.JellyfinApiConsumer;
import org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1.exceptions.UnauthorizedAccessException;
import org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1.models.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersApi {

    private final JellyfinApiConsumer jellyfinApiConsumer;

    @GetMapping
    public List<UserResponse> getAllUsers(Authentication authentication) {
        if (authentication.getAuthorities().stream().noneMatch(a ->
                a.getAuthority().equals("ADMIN")))
            throw new UnauthorizedAccessException();

        return jellyfinApiConsumer.getAllUsers().stream()
                .map(ju -> new UserResponse(ju.getId(), ju.getName(), ju.getPolicy().isAdministrator()))
                .toList();
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedAccessException.class)
    public void handleUnauthorizedAccessException() {
    }
}
