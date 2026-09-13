package dev.rjac.checks.movement;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class TimerCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "Timer";
    public TimerCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.timer.enabled", true); }
    // Primary detection is packet-level in PacketHandler; event fallback
    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        long dt = now - data.getLastMoveTime();
        // Moves faster than 1 tick (50ms)
        if (dt < 40 && dt > 0) flag(player);
        data.setLastMoveTime(now);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
