package dev.rjac.checks.movement;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class BlinkCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "Blink";
    public BlinkCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.blink.enabled", true); }
    // Primary detection is packet-level in PacketHandler; this is the event fallback
    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        var last = data.getLastLocation();
        if (last == null) return;
        double dist = player.getLocation().distance(last);
        long dt = System.currentTimeMillis() - data.getLastMoveTime();
        if (dist > 15 && dt < 200 && !player.isInsideVehicle()) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
