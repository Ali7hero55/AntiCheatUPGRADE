package dev.rjac.checks.combat;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ReachCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "Reach";
    public ReachCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.reach.enabled", true); }

    public void check(Player player, PlayerData data, Entity target) {
        if (!isEnabled()) return;
        double dist = player.getLocation().distance(target.getLocation());
        double maxReach = plugin.getConfig().getDouble("checks.reach.max-reach", 3.2);
        if (dist > maxReach) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
