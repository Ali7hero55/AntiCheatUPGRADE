package dev.rjac.listeners;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    private final RJ_AC plugin;

    public PlayerListener(RJ_AC plugin) { this.plugin = plugin; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getPlayerDataManager().get(e.getPlayer());
        plugin.getAlertManager().playerJoin(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getAlertManager().playerQuit(e.getPlayer());
        plugin.getPlayerDataManager().remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onResurrect(EntityResurrectEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        PlayerData data = plugin.getPlayerDataManager().get(player);
        plugin.getCheckManager().autoTotem.onTotemUse(player, data);
    }

    @EventHandler
    public void onBowShoot(PlayerShootBowEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        PlayerData data = plugin.getPlayerDataManager().get(player);
        plugin.getCheckManager().fastBow.onBowShoot(player, data);
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().get(player);
        // Track totem equip for AutoTotem check
        if (e.getItem().getType() == org.bukkit.Material.TOTEM_OF_UNDYING) {
            data.setLastTotemEquipTime(System.currentTimeMillis());
        }
    }
}
