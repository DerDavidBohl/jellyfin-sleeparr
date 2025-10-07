package org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1.models;

import java.time.Instant;

public record AuthResponse(
        String jwt, Instant expirationDate
        ) {
}
