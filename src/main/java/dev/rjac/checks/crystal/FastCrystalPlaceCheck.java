package dev.rjac.checks.crystal;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class FastCrystalPlaceCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "FastCrystalPlace";
    public FastCrystalPlaceCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.fastcrystalplace.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        if (now - data.getCrystalSecond() >= 1000) {
            data.setCrystalSecond(now);
            data.setCrystalPlacesPerSecond(0);
        }
        data.setCrystalPlacesPerSecond(data.getCrystalPlacesPerSecond() + 1);
        data.setLastCrystalPlaceTime(now);
        int max = plugin.getConfig().getInt("checks.fastcrystalplace.max-place-per-second", 10);
        if (data.getCrystalPlacesPerSecond() > max) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
