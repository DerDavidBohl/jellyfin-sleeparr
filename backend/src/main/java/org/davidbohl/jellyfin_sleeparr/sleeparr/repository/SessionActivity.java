package org.davidbohl.jellyfin_sleeparr.sleeparr.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionActivity {

    @Id
    private String sessionId;

    private Instant lastActivity;

    private String currentItemId;

    private int itemsWatched;

}


