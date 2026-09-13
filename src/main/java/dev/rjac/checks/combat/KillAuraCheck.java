package dev.rjac.checks.combat;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class KillAuraCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "KillAura";
    public KillAuraCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.killaura.enabled", true); }

    public void check(Player player, PlayerData data, Entity target) {
        if (!isEnabled()) return;
        // 1. Hit without arm swing packet in last 200ms
        long now = System.currentTimeMillis();
        boolean swingDetected = (now - data.getLastArmSwingPacket()) < 200;
        if (!swingDetected) { flag(player); return; }

        // 2. Multiple targets hit in same tick (MultiAura overlap)
        List<Entity> nearby = player.getNearbyEntities(4, 4, 4);
        long livingCount = nearby.stream()
                .filter(e -> e instanceof LivingEntity && !e.equals(target))
                .count();
        if (livingCount >= 3 && data.getLastAttackTime() == now) {
            flag(player);
        }
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
