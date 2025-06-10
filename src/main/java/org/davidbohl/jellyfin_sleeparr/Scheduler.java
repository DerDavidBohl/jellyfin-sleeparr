package org.davidbohl.jellyfin_sleeparr;

import lombok.extern.slf4j.Slf4j;
import org.davidbohl.jellyfin_sleeparr.jellifin_api_models.CustomQuery;
import org.davidbohl.jellyfin_sleeparr.jellifin_api_models.CustomQueryResult;
import org.davidbohl.jellyfin_sleeparr.jellifin_api_models.Session;
import org.davidbohl.jellyfin_sleeparr.repository.SleeparrJellyfinSessionInformation;
import org.davidbohl.jellyfin_sleeparr.repository.SleeparrJellyfinSessionInformationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Scheduler {

    private final JellyfinApiConsumer jellyfinApiConsumer;
    private final SleeparrJellyfinSessionInformationRepository sleeparrJellyfinSessionInformationRepository;

    @Value("${sleeparr.monitoredUserNames}")
    private String[] monitoredUserNames;

    @Value("${sleeparr.maximumInactivity}")
    private Duration maximumInactivity;

    public Scheduler(JellyfinApiConsumer jellyfinApiConsumer, SleeparrJellyfinSessionInformationRepository sleeparrJellyfinSessionInformationRepository) {
        this.jellyfinApiConsumer = jellyfinApiConsumer;
        this.sleeparrJellyfinSessionInformationRepository = sleeparrJellyfinSessionInformationRepository;
    }

    @Scheduled(fixedRateString = "5s")
    public void checkInactiveSessions() {
        List<Session> monitoredSessionsWithRunningPlayback = jellyfinApiConsumer.getActiveSessions().stream()
                .filter(s ->
                        !s.getPlayState().isPaused() &&
                                Arrays.stream(this.monitoredUserNames).anyMatch(name -> Objects.equals(name, s.getUserName()))
                )
                .toList();
        Instant minimalTimestamp = Instant.now().minus(6, ChronoUnit.HOURS);

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")  // main date time part
                // append fractional second part, variable length from 0 to 9 digits
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .optionalEnd()
                .toFormatter()
                .withZone(ZoneOffset.UTC);

        for (Session session : monitoredSessionsWithRunningPlayback) {

            String query = String.format("SELECT DateCreated, UserId, ItemId, PlayDuration " +
                    "FROM PlaybackActivity " +
                    "WHERE UserId = '%s' AND DateCreated >= '%s' " +
                    "ORDER BY ROWID DESC", session.getUserId(), formatter.format(minimalTimestamp));
            CustomQueryResult customQueryResult = this.jellyfinApiConsumer.postCustomQuery(new CustomQuery(query, false));

            List<PlaybackActivity> playbackActivities = null;
            try {
                playbackActivities = customQueryResult.getResults().stream()
                        .map(r ->
                                new PlaybackActivity(
                                        Instant.from(formatter.parse(r.getFirst())),
                                        r.get(1),
                                        r.get(2),
                                        Integer.parseInt(r.get(3))
                                )).toList();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            List<PlaybackActivity> activitiesOver3minutes = playbackActivities.stream().filter(pa -> pa.getPlayDuration() >= 10).toList();

            int secondsWatched = activitiesOver3minutes.stream().mapToInt(PlaybackActivity::getPlayDuration).sum();
            Duration watchedTime = Duration.ofSeconds(secondsWatched);

            Map<String, List<PlaybackActivity>> groupedByItem = activitiesOver3minutes.stream().collect(
                    Collectors.groupingBy(PlaybackActivity::getItemId)
            );

            Optional<SleeparrJellyfinSessionInformation> byId = this.sleeparrJellyfinSessionInformationRepository.findById(session.getId());

            boolean notPausedOrLongAgoPaused = byId.isEmpty() ||
                    byId.get().getLastStoppedBySleeparr().isBefore(Instant.now().minus(3, ChronoUnit.HOURS));

            if ( groupedByItem.size() >= 2 && watchedTime.compareTo(maximumInactivity) > 0 && notPausedOrLongAgoPaused) {
                this.jellyfinApiConsumer.pausePlayback(session.getId());
                this.jellyfinApiConsumer.sendMessage(session.getId(),
                        "Auto Stop",
                        "Your Playback was paused because Sleeparr thinks you are sleeping.",
                        Duration.ofMinutes(1).toMillis());
                this.sleeparrJellyfinSessionInformationRepository.saveAndFlush(new SleeparrJellyfinSessionInformation(session.getId(), Instant.now()));
            }
        }
    }
}

