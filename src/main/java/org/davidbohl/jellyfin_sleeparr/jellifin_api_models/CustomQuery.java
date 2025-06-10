package org.davidbohl.jellyfin_sleeparr.jellifin_api_models;

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
