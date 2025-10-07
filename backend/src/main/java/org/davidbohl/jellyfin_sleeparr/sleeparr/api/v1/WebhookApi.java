package org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1;

import lombok.RequiredArgsConstructor;
import org.davidbohl.jellyfin_sleeparr.sleeparr.PlaybackEventService;
import org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1.models.PlayBackEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
public class WebhookApi {

    private final PlaybackEventService playbackEventService;

    @PostMapping
    public void post(@RequestBody PlayBackEvent playBackEvent) {
        playbackEventService.reactToUserEvent(playBackEvent.userId(), playBackEvent.deviceId(), playBackEvent.itemId(), playBackEvent.isPaused().equals("True"));
    }

}
