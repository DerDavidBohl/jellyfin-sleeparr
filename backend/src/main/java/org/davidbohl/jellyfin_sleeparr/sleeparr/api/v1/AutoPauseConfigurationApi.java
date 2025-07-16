package org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1;


import lombok.RequiredArgsConstructor;
import org.davidbohl.jellyfin_sleeparr.security.SleeparrUserDetails;
import org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1.exceptions.BadRequestException;
import org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1.exceptions.NotFoundException;
import org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1.exceptions.UnauthorizedAccessException;
import org.davidbohl.jellyfin_sleeparr.sleeparr.repository.AutoPauseConfiguration;
import org.davidbohl.jellyfin_sleeparr.sleeparr.repository.AutoPauseConfigurationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users/{userId}/auto-pause-configuration")
@RequiredArgsConstructor
public class AutoPauseConfigurationApi {

    final AutoPauseConfigurationRepository autoPauseConfigurationRepository;

    @GetMapping
    public AutoPauseConfiguration getConfigurationForUser(@PathVariable String userId, Authentication authentication) {

        validateAuthorization(userId, authentication);

        return autoPauseConfigurationRepository.findOrCreateById(userId);
    }

    @PutMapping
    public AutoPauseConfiguration updateForUser(@PathVariable String userId, @RequestBody AutoPauseConfiguration configuraion, Authentication authentication) {
        validateAuthorization(userId, authentication);

        if (!Objects.equals(userId, configuraion.getUserId()))
            throw new BadRequestException("userId from path does not match to userId in body.");

        return autoPauseConfigurationRepository.saveAndFlush(configuraion);
    }

    private static void validateAuthorization(String userId, Authentication authentication) {
        SleeparrUserDetails principal = (SleeparrUserDetails) authentication.getPrincipal();
        if (!Objects.equals(userId, principal.getUserId()) &&
                authentication.getAuthorities().stream().noneMatch(a ->
                        a.getAuthority().equals("ADMIN")))
            throw new UnauthorizedAccessException();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BadRequestException.class)
    public void handleBadRequestException() {
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UnauthorizedAccessException.class)
    public void handleUnauthorizedAccsessException() {
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public void handleNotFoundException() {
    }

}
