package org.davidbohl.jellyfin_sleeparr.sleeparr.exceptions;

import java.text.MessageFormat;

public class UnidentifiableSession extends Exception {
    public UnidentifiableSession(String userId, String deviceId) {
        super(MessageFormat.format("Could not Identify Session from userId <{0}> and deviceId <{1}>", userId, deviceId));
    }
}
