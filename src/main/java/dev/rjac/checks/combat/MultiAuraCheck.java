package dev.rjac.checks.combat;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class MultiAuraCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "MultiAura";
    public MultiAuraCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.multiaura.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        List<Entity> nearby = player.getNearbyEntities(5, 5, 5);
        long hits = nearby.stream().filter(e -> e instanceof LivingEntity).count();
        if (hits >= 3 && System.currentTimeMillis() - data.getLastAttackTime() < 50) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
