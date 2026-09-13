package dev.rjac.listeners;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

public class CombatListener implements Listener {

    private final RJ_AC plugin;
    public CombatListener(RJ_AC plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;
        if (player.hasPermission("rjac.bypass")) return;
        Entity target = e.getEntity();
        PlayerData data = plugin.getPlayerDataManager().get(player);

        long now = System.currentTimeMillis();
        data.setLastAttackTime(now);

        var cm = plugin.getCheckManager();
        cm.killAura.check(player, data, target);
        cm.reach.check(player, data, target);
        cm.aimBot.check(player, data, target);
        cm.autoClicker.check(player, data);
        cm.noSwing.check(player, data);
        cm.hitBox.check(player, data, target);
        cm.backtrack.check(player, data, target);
        cm.multiAura.check(player, data);

        // CritSpam: check if this is a crit
        if (e.isCritical()) cm.critSpam.check(player, data);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        PlayerData data = plugin.getPlayerDataManager().get(player);
        data.setLastDamageTime(System.currentTimeMillis());

        // Velocity check: expect knockback after taking damage
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            // Schedule a velocity check 1 tick later
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    plugin.getCheckManager().velocity.check(player, data, player.getVelocity());
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onSwing(PlayerAnimationEvent e) {
        PlayerData data = plugin.getPlayerDataManager().get(e.getPlayer());
        data.setLastArmSwingPacket(System.currentTimeMillis());
        data.setSwungThisTick(true);
    }
}
