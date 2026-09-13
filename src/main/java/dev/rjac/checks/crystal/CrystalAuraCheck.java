package dev.rjac.checks.crystal;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class CrystalAuraCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "CrystalAura";
    public CrystalAuraCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.crystalaura.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        // Place + explode cycle under 100ms = crystal aura
        long cycle = now - data.getLastCrystalPlaceTime();
        if (data.getLastCrystalPlaceTime() > 0 && cycle < 100) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
