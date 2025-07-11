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
public class JellyfinUser {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Policy")
    private JellyfinUserPolicy policy;
}
