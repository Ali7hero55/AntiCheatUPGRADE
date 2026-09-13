package dev.rjac.checks.misc;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class BadPacketsCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "BadPackets";
    public BadPacketsCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.badpackets.enabled", true); }
    // Primary detection via PacketHandler; event-layer fallback
    public void flag(Player player) {
        if (!isEnabled()) return;
        plugin.getViolationManager().flag(player, CHECK);
    }
}
