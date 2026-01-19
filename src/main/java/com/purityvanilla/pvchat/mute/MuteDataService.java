package com.purityvanilla.pvchat.mute;

import com.purityvanilla.pvlib.database.DataService;
import com.purityvanilla.pvlib.database.DatabaseConnector;
import com.purityvanilla.pvlib.util.CacheHelper;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MuteDataService extends DataService {
    private final MuteOperator operator;
    private final ConcurrentHashMap<UUID, PlayerMuteStatus> muteCache;

    public MuteDataService(JavaPlugin plugin, DatabaseConnector database) {
        super(plugin);

        operator = new MuteOperator(database);
        muteCache = new ConcurrentHashMap<>();
    }

    @Override
    public void saveAll() {
        for (PlayerMuteStatus muteStatus : muteCache.values()) {
            if (muteStatus.isMuted()) operator.savePlayerMuteData(muteStatus);
        }
    }

    @Override
    public void cleanCache() {
        for (UUID absentPlayer : CacheHelper.getAbsentUUIDs(muteCache.keySet(), plugin)) {
            unloadMuteStatus(muteCache.get(absentPlayer));
        }
    }

    private boolean isCached(UUID playerID) {
        return muteCache.containsKey(playerID);
    }

    private void unloadMuteStatus(PlayerMuteStatus muteStatus) {
        if (muteStatus.isMuted()) operator.savePlayerMuteData(muteStatus);
        muteCache.remove(muteStatus.getPlayerID());
    }

    public PlayerMuteStatus getMuteStatus(UUID playerID) {
        if (isCached(playerID)) {
            return muteCache.get(playerID);
        }

        PlayerMuteStatus muteStatus = operator.getPlayerMuteData(playerID);
        muteCache.put(playerID, muteStatus);
        return muteStatus;
    }

    public boolean isPlayerMuted(UUID playerID) {
        return getMuteStatus(playerID).isMuted();
    }

    public void mutePlayer(UUID playerID, Timestamp expiration) {
        PlayerMuteStatus muteStatus = new PlayerMuteStatus(playerID, true, expiration);
        operator.savePlayerMuteData(muteStatus);
        muteCache.put(playerID, muteStatus);
    }

    public void permMutePlayer(UUID playerID) {
        mutePlayer(playerID, null);
    }

    public void unMutePlayer(UUID playerID) {
        PlayerMuteStatus muteStatus = new PlayerMuteStatus(playerID, false, null);
        muteCache.put(playerID, muteStatus);
        operator.removePlayerMuteData(playerID);
    }
}
