package dev.rjac.checks.combat;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class VelocityCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "Velocity";
    public VelocityCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.velocity.enabled", true); }

    public void check(Player player, PlayerData data, Vector expectedKnockback) {
        if (!isEnabled()) return;
        double min = plugin.getConfig().getDouble("checks.velocity.min-knockback", 0.35);
        Vector actual = player.getVelocity();
        double ratio = actual.length() / Math.max(expectedKnockback.length(), 0.001);
        if (ratio < min) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
