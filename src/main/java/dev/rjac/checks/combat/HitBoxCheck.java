package dev.rjac.checks.combat;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class HitBoxCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "HitBox";
    public HitBoxCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.hitbox.enabled", true); }

    public void check(Player player, PlayerData data, Entity target) {
        if (!isEnabled()) return;
        double dist = player.getLocation().distance(target.getLocation());
        // HitBox expanders let them hit from farther than reach but not detectable as reach
        if (dist > 3.8 && dist <= 5.0) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
