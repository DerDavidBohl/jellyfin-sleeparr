package org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1;

import org.davidbohl.jellyfin_sleeparr.jellyfin.api.JellyfinApiConsumer;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.models.AuthenticationResponse;
import org.davidbohl.jellyfin_sleeparr.security.JwtService;
import org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1.models.AuthRequest;
import org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1.models.AuthResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/login")
public class LoginApi {
    private final JellyfinApiConsumer jellyfinApiConsumer;
    private final JwtService jwtService;

    public LoginApi(JellyfinApiConsumer jellyfinApiConsumer, JwtService jwtService) {
        this.jellyfinApiConsumer = jellyfinApiConsumer;
        this.jwtService = jwtService;
    }

    @PostMapping
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        AuthenticationResponse authenticationResponse = this.jellyfinApiConsumer.authenticate(authRequest.username(), authRequest.password());

        Date expiration = new Date(new Date().getTime() + Duration.ofDays(1).toMillis());

        String jwt = jwtService.generateToken(authenticationResponse.getUser().getId(), expiration);
        return new AuthResponse(jwt, expiration.toInstant());
    }

}
