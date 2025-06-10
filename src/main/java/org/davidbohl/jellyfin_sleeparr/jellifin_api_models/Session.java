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
public class Session {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("LastActivityDate")
    private String lastActivityDate;

    @JsonProperty("LastPlaybackCheckIn")
    private String lastPlaybackCheckIn;

    @JsonProperty("UserId")
    private String userId;

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("PlayState")
    private SessionPlayState playState;
}
