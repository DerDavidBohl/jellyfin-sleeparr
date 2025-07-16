package org.davidbohl.jellyfin_sleeparr.sleeparr.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutoPauseConfiguration {

    @Id
    @NotNull
    String userId;

    @NotNull
    Duration watchDuration;

    @NotNull
    int differentItems;

    @NotNull
    boolean enabled;

    public static AutoPauseConfiguration defaultValue(String userId) {
        return new AutoPauseConfiguration(userId, Duration.ofHours(3), 3, true);
    }
}


