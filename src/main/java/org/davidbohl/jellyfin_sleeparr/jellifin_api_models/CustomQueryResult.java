package org.davidbohl.jellyfin_sleeparr.jellifin_api_models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomQueryResult {
    private List<String> columns;
    private List<List<String>> results;
    private String message;
}
