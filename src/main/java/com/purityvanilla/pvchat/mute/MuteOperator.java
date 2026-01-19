package com.purityvanilla.pvchat.mute;

import com.purityvanilla.pvlib.database.DatabaseConnector;
import com.purityvanilla.pvlib.database.DatabaseOperator;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class MuteOperator extends DatabaseOperator {

    public MuteOperator(DatabaseConnector database) {
        super(database);
    }

    @Override
    protected void createTables() {
        String query = """
                CREATE TABLE IF NOT EXISTS player_mutes (
                    playerID CHAR(36) PRIMARY KEY,
                    expiration TIMESTAMP DEFAULT NULL,
                    CONSTRAINT fk_playerID FOREIGN KEY (playerID) REFERENCES players (uuid) ON DELETE CASCADE
                )
                """;
        database.executeUpdate(query);
    }

    public PlayerMuteStatus getPlayerMuteData(UUID playerID) {
        String query = "SELECT playerID, expiration FROM player_mutes WHERE playerID = ?";
        List<Object> params = List.of(playerID);

        return database.executeQuery(query, params, rs -> {
            if (rs.next()) {
                return new PlayerMuteStatus(
                        UUID.fromString(rs.getString("playerID")),
                        true,
                        rs.getTimestamp("expiration")
                );
            }
            return new PlayerMuteStatus(playerID, false, null);
        });
    }

    public void savePlayerMuteData(UUID playerID, Timestamp expiration) {
        String query = "INSERT INTO players (playerID, expiration) VALUES (?, ?)";
        List<Object> params = List.of(playerID, expiration);
        database.executeUpdate(query, params);
    }

    public void savePlayerMuteData(PlayerMuteStatus mute) {
        if (mute.isMuted()) savePlayerMuteData(mute.getPlayerID(), mute.getExpiration());
    }

    public void removePlayerMuteData(UUID playerID) {
        String query = "DELETE FROM players WHERE playerID = ?";
        List<Object> params = List.of(playerID);
        database.executeUpdate(query, params);
    }
}
