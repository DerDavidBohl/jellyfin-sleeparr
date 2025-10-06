package org.davidbohl.jellyfin_sleeparr.sleeparr.api.v1.models;

public record PlayBackEvent(String userId, String deviceId, String isAutomated, String itemId) {
}
