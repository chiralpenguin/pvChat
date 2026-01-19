package com.purityvanilla.pvchat.mute;

import java.sql.Timestamp;
import java.util.UUID;

public class PlayerMuteStatus {
    private final UUID playerID;
    private final boolean isMuted;
    private final Timestamp expiration;

    public PlayerMuteStatus(UUID playerID, boolean muted) {
        this.playerID = playerID;
        this.isMuted = muted;
        this.expiration = null;
    }

    public PlayerMuteStatus(UUID playerID, boolean muted, Timestamp expirationDate) {
        this.playerID = playerID;
        this.isMuted = muted;
        this.expiration = expirationDate;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public Timestamp getExpiration() {
        return expiration;
    }

    public boolean expires() {
        return expiration != null;
    }
}
