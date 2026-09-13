package dev.rjac.checks.movement;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class NoFallCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "NoFall";
    public NoFallCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.nofall.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        if (player.getAllowFlight() || player.isGliding() || player.isInsideVehicle()) return;
        // Fell more than 3 blocks but took no fall damage and wasn't in water/climbable
        if (data.getLastDeltaY() < -3.0 && player.getFallDistance() == 0 && !player.isInWater()) {
            flag(player);
        }
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
