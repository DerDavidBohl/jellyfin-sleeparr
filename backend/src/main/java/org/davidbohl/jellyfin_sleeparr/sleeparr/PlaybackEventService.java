package org.davidbohl.jellyfin_sleeparr.sleeparr;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.JellyfinApiConsumer;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.models.Session;
import org.davidbohl.jellyfin_sleeparr.sleeparr.exceptions.UnidentifiableSession;
import org.davidbohl.jellyfin_sleeparr.sleeparr.repository.AutoPauseConfiguration;
import org.davidbohl.jellyfin_sleeparr.sleeparr.repository.AutoPauseConfigurationRepository;
import org.davidbohl.jellyfin_sleeparr.sleeparr.repository.SessionActivity;
import org.davidbohl.jellyfin_sleeparr.sleeparr.repository.SessionActivityRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@AllArgsConstructor
@Slf4j
public class PlaybackEventService {

    private final SessionActivityRepository sessionActivityRepository;
    private final JellyfinApiConsumer jellyfinApiConsumer;
    private final AutoPauseConfigurationRepository autoPauseConfigurationRepository;

    public void reactToUserEvent(String userId, String deviceId, String itemId, boolean isPaused) {
        try {
            userId = userId.replace("-", "");

            log.info("Got event: IsPaused <{}> User <{}> Device <{}> itemId <{}>", isPaused, userId, deviceId, itemId);

            AutoPauseConfiguration configuration = autoPauseConfigurationRepository.findOrCreateById(userId);

            if (!configuration.isEnabled())
                return;

            Session session = identifySession(userId, deviceId);

            SessionActivity sessionActivity = updateSessionActivity(session, itemId, isPaused);


            pauseSessionIfInactive(configuration, session, sessionActivity);

        } catch (UnidentifiableSession e) {
            log.warn("Could not Identify Session", e);
        }
    }

    private void pauseSessionIfInactive(AutoPauseConfiguration configuration, Session session, SessionActivity sessionActivity) {
        if (sessionActivity.getLastActivity()
                .plus(configuration.getWatchDuration())
                .isBefore(Instant.now()) &&
                sessionActivity.getItemsWatched() >= configuration.getDifferentItems()) {

            log.info("Pausing Playback for user '{}' in session '{}'", session.getUserName(), session.getId());
            this.jellyfinApiConsumer.pausePlayback(session.getId());
            this.jellyfinApiConsumer.sendMessage(session.getId(),
                    "Auto Stop",
                    "Your Playback was paused because Sleeparr thinks you are sleeping.",
                    Duration.ofMinutes(1).toMillis());
            sessionActivity.setLastActivity(Instant.now());
            sessionActivity.setItemsWatched(0);
            this.sessionActivityRepository.saveAndFlush(sessionActivity);
        }
    }

    private SessionActivity updateSessionActivity(Session session, String itemId, boolean isPaused) {
        SessionActivity sessionActivity = sessionActivityRepository.findById(session.getId()).orElse(new SessionActivity(session.getId(), Instant.now(), itemId, 0));

        if (!isPaused && sessionActivity.getSessionId().equals(itemId))
            return sessionActivity;

        if (isPaused && !sessionActivity.getCurrentItemId().equals(itemId)) {
            sessionActivity.setCurrentItemId(itemId);
            sessionActivity.setItemsWatched(sessionActivity.getItemsWatched() + 1);
        }

        if (isPaused && sessionActivity.getCurrentItemId().equals(itemId))
            sessionActivity.setLastActivity(Instant.now());

        return sessionActivityRepository.saveAndFlush(sessionActivity);
    }

    private Session identifySession(String userId, String deviceId) throws UnidentifiableSession {
        return jellyfinApiConsumer.getActiveSessions().stream().filter(session -> session.getDeviceId().equals(deviceId) && session.getUserId().equals(userId)).findFirst().orElseThrow(() -> new UnidentifiableSession(userId, deviceId));
    }


}
