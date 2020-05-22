package pl.extollite.hidenseek.manager;

import cn.nukkit.Player;
import lombok.Getter;
import pl.extollite.hidenseek.data.PlayerData;
import pl.extollite.hidenseek.game.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Getter
public class PlayerManager {

    private final Map<UUID, PlayerData> playerMap;

    public PlayerManager() {
        this.playerMap = new HashMap<>();
    }

    public boolean hasPlayerData(Player player) {
        return hasPlayerData(player.getUniqueId());
    }

    public boolean hasPlayerData(UUID uuid) {
        return playerMap.containsKey(uuid);
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID uuid) {
        if (hasPlayerData(uuid)) {
            return playerMap.get(uuid);
        }
        return null;
    }

    public PlayerData getData(Player player) {
        return getData(player.getUniqueId());
    }

    public PlayerData getData(UUID uuid) {
        if (hasPlayerData(uuid))
            return getPlayerData(uuid);
        else
            return null;
    }

    public boolean hasData(Player player) {
        return hasData(player.getUniqueId());
    }

    public boolean hasData(UUID uuid) {
        return hasPlayerData(uuid);
    }

    public void addPlayerData(PlayerData playerData) {
        playerMap.put(playerData.getPlayer().getUniqueId(), playerData);
    }

    public void removePlayerData(Player player) {
        this.playerMap.remove(player.getUniqueId());
    }

    public void removePlayerData(UUID uuid) {
        this.playerMap.remove(uuid);
    }

    public Game getGame(Player player) {
        return getGame(player.getUniqueId());
    }

    public Game getGame(UUID uuid) {
        if (hasPlayerData(uuid))
            return getPlayerData(uuid).getGame();
        else
            return null;
    }

}
