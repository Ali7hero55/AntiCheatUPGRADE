package dev.rjac.checks.combat;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BacktrackCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "Backtrack";
    public BacktrackCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.backtrack.enabled", true); }

    public void check(Player player, PlayerData data, Entity target) {
        if (!isEnabled()) return;
        // Hit registered but target is farther than max reach from ALL stored history positions
        double maxReach = plugin.getConfig().getDouble("checks.reach.max-reach", 3.2) + 1.5;
        boolean couldHit = data.getLocationHistory().stream()
                .anyMatch(loc -> loc.distance(target.getLocation()) <= maxReach);
        if (!couldHit) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
