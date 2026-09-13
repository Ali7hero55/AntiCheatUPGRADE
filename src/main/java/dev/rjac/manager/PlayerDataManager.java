package dev.rjac.manager;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerDataManager {

    private final RJ_AC plugin;
    private final Map<UUID, PlayerData> dataMap = new HashMap<>();

    public PlayerDataManager(RJ_AC plugin) {
        this.plugin = plugin;
    }

    public PlayerData get(Player player) {
        return dataMap.computeIfAbsent(player.getUniqueId(), k -> new PlayerData(player));
    }

    public PlayerData get(UUID uuid) {
        return dataMap.get(uuid);
    }

    public void remove(UUID uuid) {
        dataMap.remove(uuid);
    }

    public Collection<PlayerData> getAll() {
        return dataMap.values();
    }
}
