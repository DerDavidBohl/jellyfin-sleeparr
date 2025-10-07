package org.davidbohl.jellyfin_sleeparr.sleeparr.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
public class AutoPauseConfigurationRepositoryImpl implements CustomAutoPauseConfigurationRepository {

    @Value("${sleeparr.defaults.watchDuration:3h}")
    private Duration defaultWatchDuration;

    @Value("${sleeparr.defaults.differentItems:3}")
    private int defaultDifferentItems;

    @Value("${sleeparr.defaults.enabled:true}")
    private boolean defaultEnabled;

    @Lazy
    @Autowired
    private AutoPauseConfigurationRepository autoPauseConfigurationRepository;

    @Override
    public AutoPauseConfiguration findOrCreateById(String userId) {
        Optional<AutoPauseConfiguration> optional = autoPauseConfigurationRepository.findById(userId);

        return optional.orElseGet(() -> autoPauseConfigurationRepository.saveAndFlush(
                new AutoPauseConfiguration(userId, defaultWatchDuration, defaultDifferentItems, defaultEnabled) // Create new if not fount in DB
        ));

    }
}
