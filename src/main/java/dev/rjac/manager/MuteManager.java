package dev.rjac.manager;

import dev.rjac.RJ_AC;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.*;

public class MuteManager {

    private final RJ_AC plugin;
    // uuid -> expiry timestamp (-1 = permanent)
    private final Map<UUID, Long>   muteExpiry  = new HashMap<>();
    private final Map<UUID, String> muteReasons = new HashMap<>();

    public MuteManager(RJ_AC plugin) { this.plugin = plugin; }

    public void mute(Player player, String duration, String reason) {
        long expiry = parseDuration(duration);
        muteExpiry.put(player.getUniqueId(), expiry);
        muteReasons.put(player.getUniqueId(), reason);
        player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(
                plugin.tag() + RJ_AC.color("&cYou have been muted. Reason: &f" + reason
                        + (expiry == -1 ? " &7(permanent)" : " &7for &f" + duration))));
    }

    public void unmute(UUID uuid) {
        muteExpiry.remove(uuid);
        muteReasons.remove(uuid);
    }

    public boolean isMuted(Player player) {
        UUID uuid = player.getUniqueId();
        if (!muteExpiry.containsKey(uuid)) return false;
        long expiry = muteExpiry.get(uuid);
        if (expiry != -1 && System.currentTimeMillis() > expiry) {
            muteExpiry.remove(uuid);
            muteReasons.remove(uuid);
            return false;
        }
        return true;
    }

    public String getReason(UUID uuid) {
        return muteReasons.getOrDefault(uuid, "No reason given");
    }

    private long parseDuration(String duration) {
        if (duration.equalsIgnoreCase("perm") || duration.equalsIgnoreCase("permanent")) return -1;
        long now = System.currentTimeMillis();
        long millis = 0;
        StringBuilder num = new StringBuilder();
        for (char c : duration.toCharArray()) {
            if (Character.isDigit(c)) { num.append(c); }
            else {
                long n = num.length() > 0 ? Long.parseLong(num.toString()) : 0;
                num = new StringBuilder();
                millis += switch (c) {
                    case 's' -> n * 1000L;
                    case 'm' -> n * 60_000L;
                    case 'h' -> n * 3_600_000L;
                    case 'd' -> n * 86_400_000L;
                    case 'w' -> n * 604_800_000L;
                    default  -> 0L;
                };
            }
        }
        return millis > 0 ? now + millis : now + 86_400_000L;
    }
}
