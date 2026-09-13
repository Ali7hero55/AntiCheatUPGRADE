package dev.rjac.checks.movement;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class FlightCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "Flight";
    public FlightCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.flight.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        if (player.getAllowFlight() || player.isFlying() || player.isGliding()
                || player.isInsideVehicle() || player.hasPotionEffect(PotionEffectType.LEVITATION)) return;
        if (!data.isOnGround() && data.getAirTicks() > 20 && data.getLastDeltaY() >= -0.01) {
            flag(player);
        }
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
