package dev.rjac.checks.crystal;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class AutoCrystalCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "AutoCrystal";
    public AutoCrystalCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.autocrystal.enabled", true); }

    public void checkExplode(Player player, PlayerData data) {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        long threshold = plugin.getConfig().getLong("checks.autocrystal.min-place-explode-ms", 50);
        long delta = now - data.getLastCrystalPlaceTime();
        if (data.getLastCrystalPlaceTime() > 0 && delta < threshold) flag(player);
        data.setLastCrystalExplodeTime(now);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
