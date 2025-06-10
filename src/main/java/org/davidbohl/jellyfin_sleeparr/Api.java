package org.davidbohl.jellyfin_sleeparr;

import org.davidbohl.jellyfin_sleeparr.jellyfin.api.models.CustomQuery;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.models.CustomQueryResult;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.models.Session;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.JellyfinApiConsumer;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@RestController()
@RequestMapping("/api/v1")
public class Api {

    private final JellyfinApiConsumer jellyfinApiConsumer;
    private final Scheduler scheduler;

    public Api(JellyfinApiConsumer jellyfinApiConsumer, Scheduler scheduler) {
        this.jellyfinApiConsumer = jellyfinApiConsumer;
        this.scheduler = scheduler;
    }

    @PostMapping("/stop-inactive")
    public void postStopInactive() {
        this.scheduler.checkInactiveSessions();
    }

    @GetMapping("/sessions")
    public List<Session> getSessions() {
        return this.jellyfinApiConsumer.getActiveSessions();
    }

    @PostMapping("/sessions/{sessionId}/go-home")
    public void postGoHome(@PathVariable String sessionId) {
        this.jellyfinApiConsumer.goHome(sessionId);
    }

    @PostMapping("/sessions/{sessionId}/pause-playback")
    public void postPausePlayback(@PathVariable String sessionId) {
        this.jellyfinApiConsumer.pausePlayback(sessionId);
    }

    @PostMapping("/sessions/{sessionId}/send-message")
    public void postSendMessage(@PathVariable String sessionId) {
        this.jellyfinApiConsumer.sendMessage(sessionId, "Hello", "this message should be displayed for 10 seconds.", 10000);
    }

    @GetMapping("/data/{userName}")
    public List<List<String>> getDataForUserName(@PathVariable String userName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC);

        Instant fromDate = Instant.now().minus(1, ChronoUnit.DAYS);
        String customQueryString = "SELECT UserId, * FROM PlaybackActivity " +
                "WHERE DateCreated >= '%s'".formatted(formatter.format(fromDate));
        CustomQueryResult customQueryResult = this.jellyfinApiConsumer.postCustomQuery(new CustomQuery(customQueryString, true));

        return customQueryResult.getResults().stream().filter(s -> Objects.equals(s.getFirst(), userName)).toList();
    }

}
