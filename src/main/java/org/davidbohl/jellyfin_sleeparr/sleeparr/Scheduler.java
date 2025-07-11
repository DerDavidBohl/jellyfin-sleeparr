package org.davidbohl.jellyfin_sleeparr.sleeparr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.JellyfinApiConsumer;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.models.CustomQuery;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.models.CustomQueryResult;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.models.Session;
import org.davidbohl.jellyfin_sleeparr.sleeparr.models.PlaybackActivity;
import org.davidbohl.jellyfin_sleeparr.sleeparr.repository.AutoPauseConfiguration;
import org.davidbohl.jellyfin_sleeparr.sleeparr.repository.AutoPauseConfigurationRepository;
import org.davidbohl.jellyfin_sleeparr.sleeparr.repository.SessionPauseTimeStamp;
import org.davidbohl.jellyfin_sleeparr.sleeparr.repository.SessionPauseTimeStampRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {

    private final JellyfinApiConsumer jellyfinApiConsumer;
    private final SessionPauseTimeStampRepository sessionPauseTimeStampRepository;
    private final AutoPauseConfigurationRepository autoPauseConfigurationRepository;

    @Scheduled(fixedRateString = "5s")
    public void checkInactiveSessions() {
        List<Session> monitoredSessionsWithRunningPlayback = jellyfinApiConsumer.getActiveSessions().stream()
                .filter(s ->
                        !s.getPlayState().isPaused() &&
                                s.getPlayState().getPositionTicks() > 0
                )
                .toList();

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")  // main date time part
                // append fractional second part, variable length from 0 to 9 digits
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .optionalEnd()
                .toFormatter()
                .withZone(ZoneOffset.UTC);


        String query = String.format("SELECT DateCreated, UserId, ItemId, PlayDuration, ClientName, DeviceName " +
                "FROM PlaybackActivity " +
                "WHERE DateCreated >= '%s' " +
                "ORDER BY ROWID DESC", formatter.format(Instant.now().minus(2, ChronoUnit.DAYS)));

        CustomQueryResult customQueryResult = this.jellyfinApiConsumer.postCustomQuery(new CustomQuery(query, false));

        List<PlaybackActivity> playbackActivities;
        try {
            playbackActivities = customQueryResult.getResults().stream()
                    .map(r ->
                            new PlaybackActivity(
                                    Instant.from(formatter.parse(r.getFirst())),
                                    r.get(1),
                                    r.get(2),
                                    Integer.parseInt(r.get(3)),
                                    r.get(4),
                                    r.get(5)
                            )).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (Session session : monitoredSessionsWithRunningPlayback) {


            List<PlaybackActivity> relevantPlaybackActivities = playbackActivities.stream().filter(pa ->
                    pa.getDateCreated().isAfter(Instant.now().minus(6, ChronoUnit.HOURS)) &&
                            pa.getPlayDuration() >= 10 &&
                            Objects.equals(pa.getDeviceName(), session.getDeviceName()) &&
                            Objects.equals(pa.getClientName(), session.getClient())
            ).toList();

            int secondsWatched = relevantPlaybackActivities.stream().mapToInt(PlaybackActivity::getPlayDuration).sum();
            Duration watchedTime = Duration.ofSeconds(secondsWatched);

            Map<String, List<PlaybackActivity>> groupedByItem = relevantPlaybackActivities.stream().collect(
                    Collectors.groupingBy(PlaybackActivity::getItemId)
            );

            AutoPauseConfiguration autoPauseConfiguration = autoPauseConfigurationRepository
                    .findOrCreateById(session.getUserId());

            boolean watchedEnoughItems = groupedByItem.size() >= autoPauseConfiguration.getDifferentItems();
            boolean watchedLongEnough = watchedTime.compareTo(autoPauseConfiguration.getWatchDuration()) > 0;
            boolean isEnabled = autoPauseConfiguration.isEnabled();

            Optional<SessionPauseTimeStamp> sleeparrJellyfinSessionInformation = this.sessionPauseTimeStampRepository.findById(session.getId());

            boolean notPausedInLastConfiguredTime = sleeparrJellyfinSessionInformation.isEmpty() ||
                    sleeparrJellyfinSessionInformation.get().getLastPause()
                            .isBefore(Instant.now().minus(autoPauseConfiguration.getWatchDuration()));

            if (isEnabled &&
                    watchedEnoughItems &&
                    watchedLongEnough &&
                    notPausedInLastConfiguredTime) {

                log.info("Pausing Playback for user '{}' in session '{}'", session.getUserName(), session.getId());

                this.jellyfinApiConsumer.pausePlayback(session.getId());
                this.jellyfinApiConsumer.sendMessage(session.getId(),
                        "Auto Stop",
                        "Your Playback was paused because Sleeparr thinks you are sleeping.",
                        Duration.ofMinutes(1).toMillis());
                this.sessionPauseTimeStampRepository.saveAndFlush(new SessionPauseTimeStamp(session.getId(), Instant.now()));
            }
        }
    }

    private boolean isAutoPauseConfigurationCriteriaMet(AutoPauseConfiguration apc, int diffrentItemCount, Duration watchedDuration) {
        return apc.isEnabled() &&
                diffrentItemCount >= apc.getDifferentItems() &&
                watchedDuration.compareTo(apc.getWatchDuration()) > 0;
    }
}

