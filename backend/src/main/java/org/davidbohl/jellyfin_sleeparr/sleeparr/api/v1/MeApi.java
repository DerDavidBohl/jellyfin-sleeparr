package org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1;

import org.davidbohl.jellyfin_sleeparr.security.SleeparrUserDetails;
import org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1.models.UserResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class MeApi {

    @GetMapping
    public UserResponse getCurrentUser(Authentication auth) {
        SleeparrUserDetails principal = (SleeparrUserDetails) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        return new UserResponse(principal.getUserId(), principal.getUsername(), isAdmin);
    }

}