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
public class Message {
    @JsonProperty("Header")
    private String header;

    @JsonProperty("Text")
    private String text;

    @JsonProperty("TimeoutMs")
    private long timeoutMs;
}
