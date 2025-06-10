package org.davidbohl.jellyfin_sleeparr.jellyfin.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomQuery {
    private String customQueryString;
    private boolean replaceUserId;
}
