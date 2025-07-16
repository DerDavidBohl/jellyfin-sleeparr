package org.davidbohl.jellyfin_sleeparr.sleeparr.repository;

public interface CustomAutoPauseConfigurationRepository {
    AutoPauseConfiguration findOrCreateById(String userId);
}
