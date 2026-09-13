package dev.rjac.listeners;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;

public class CrystalListener implements Listener {

    private final RJ_AC plugin;
    public CrystalListener(RJ_AC plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("rjac.bypass")) return;
        if (e.getItem() == null || e.getItem().getType() != Material.END_CRYSTAL) return;
        PlayerData data = plugin.getPlayerDataManager().get(player);
        var cm = plugin.getCheckManager();
        cm.fastCrystalPlace.check(player, data);
        cm.crystalAura.check(player, data);
        cm.autoPot.check(player, data);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent e) {
        if (!(e.getEntity() instanceof EnderCrystal)) return;
        // Find nearest player who placed this crystal
        e.getEntity().getNearbyEntities(10, 10, 10).stream()
                .filter(en -> en instanceof Player)
                .map(en -> (Player) en)
                .filter(p -> !p.hasPermission("rjac.bypass"))
                .forEach(player -> {
                    PlayerData data = plugin.getPlayerDataManager().get(player);
                    plugin.getCheckManager().autoCrystal.checkExplode(player, data);
                });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof EnderCrystal)) return;
        if (!(e.getEntity() instanceof Player player)) return;
        if (player.hasPermission("rjac.bypass")) return;
        PlayerData data = plugin.getPlayerDataManager().get(player);
        plugin.getCheckManager().selfCrystal.check(player, data, e.getDamage());
    }
}
