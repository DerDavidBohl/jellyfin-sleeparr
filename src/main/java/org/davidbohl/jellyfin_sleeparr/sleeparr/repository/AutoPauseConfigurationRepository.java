package org.davidbohl.jellyfin_sleeparr.sleeparr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoPauseConfigurationRepository extends JpaRepository<AutoPauseConfiguration, String>, CustomAutoPauseConfigurationRepository {

}

