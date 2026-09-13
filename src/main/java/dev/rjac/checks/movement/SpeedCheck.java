package dev.rjac.checks.movement;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SpeedCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "Speed";
    public SpeedCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.speed.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        if (player.isFlying() || player.isInsideVehicle() || player.isGliding()) return;
        var last = data.getLastLocation();
        if (last == null) return;
        double base = 0.22;
        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            int amp = player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() + 1;
            base += amp * 0.054;
        }
        double mult = plugin.getConfig().getDouble("checks.speed.max-speed-multiplier", 1.4);
        double dx = player.getLocation().getX() - last.getX();
        double dz = player.getLocation().getZ() - last.getZ();
        double spd = Math.sqrt(dx*dx + dz*dz);
        if (spd > base * mult) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
