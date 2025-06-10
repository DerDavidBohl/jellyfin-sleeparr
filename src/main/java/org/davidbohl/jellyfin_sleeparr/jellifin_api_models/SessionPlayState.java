package org.davidbohl.jellyfin_sleeparr.jellifin_api_models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionPlayState {

    @JsonProperty("CanSeek")
    private boolean canSeek;

    @JsonProperty("IsPaused")
    private boolean isPaused;

    @JsonProperty("PositionTicks")
    private long positionTicks;

    @JsonProperty("MediaSourceId")
    private String mediaSourceId;

}
