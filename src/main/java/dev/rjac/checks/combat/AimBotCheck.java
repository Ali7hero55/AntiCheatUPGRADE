package dev.rjac.checks.combat;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AimBotCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "AimBot";
    public AimBotCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.aimbot.enabled", true); }

    public void check(Player player, PlayerData data, Entity target) {
        if (!isEnabled()) return;
        Vector toTarget = target.getLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
        Vector dir = player.getEyeLocation().getDirection();
        double dot = dir.dot(toTarget);
        // Perfect aim (dot > 0.999) on a moving target while player yaw changes > 45 deg/tick = aimbot
        float yawDiff = Math.abs(player.getLocation().getYaw() - (data.getLastLocation() != null ? data.getLastLocation().getYaw() : player.getLocation().getYaw()));
        if (dot > 0.999 && yawDiff > 45) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
