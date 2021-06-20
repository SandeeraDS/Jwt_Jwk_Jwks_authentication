package com.sandeera.authserver.jwtConfig;

import java.security.KeyPair;
import java.util.Calendar;
import java.util.TimeZone;

import lombok.Data;

@Data
public class KeyPairContainer {

    private String keyId;
    private KeyPair keyPair;
    private final Long expiredAt;

    public KeyPairContainer(String keyId, KeyPair keyPair, Long secondsExpiredAfter) {
        this.keyId = keyId;
        this.keyPair = keyPair;

        Long currentUTCSeconds = getUtcCalendar().getTimeInMillis() / 1000L;

        //expiration time in the future
        this.expiredAt = currentUTCSeconds + secondsExpiredAfter;
    }

    public boolean isExpired() {
        Long currentSeconds = getUtcCalendar().getTimeInMillis() / 1000L;
        return currentSeconds > expiredAt;
    }

    private Calendar getUtcCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }
}