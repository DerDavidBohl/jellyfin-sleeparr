package org.davidbohl.jellyfin_sleeparr.jellyfin.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JellyfinUserPolicy {

    @JsonProperty("IsAdministrator")
    private boolean isAdministrator;

}
