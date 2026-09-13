package dev.rjac.checks.crystal;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class SelfCrystalCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "SelfCrystal";
    public SelfCrystalCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.selfcrystal.enabled", true); }

    // Called when a crystal explodes near the player who placed it — zero damage taken = suspicious
    public void check(Player player, PlayerData data, double damage) {
        if (!isEnabled()) return;
        if (damage <= 0.0 && data.getLastCrystalPlaceTime() > 0
                && System.currentTimeMillis() - data.getLastCrystalPlaceTime() < 500) {
            flag(player);
        }
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
