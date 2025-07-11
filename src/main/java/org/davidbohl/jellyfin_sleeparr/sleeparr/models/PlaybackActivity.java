package org.davidbohl.jellyfin_sleeparr.sleeparr.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaybackActivity {
    private Instant dateCreated;
    private String userId;
    private String itemId;
    private int playDuration;
    private String clientName;
    private String deviceName;
}
