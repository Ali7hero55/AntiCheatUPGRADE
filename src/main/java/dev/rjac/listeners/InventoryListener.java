package dev.rjac.listeners;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryListener implements Listener {

    private final RJ_AC plugin;
    public InventoryListener(RJ_AC plugin) { this.plugin = plugin; }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;
        plugin.getPlayerDataManager().get(player).setInventoryOpen(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;
        plugin.getPlayerDataManager().get(player).setInventoryOpen(false);
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().get(player);
        if (player.getInventory().getItem(e.getNewSlot()) != null
                && player.getInventory().getItem(e.getNewSlot()).getType() == org.bukkit.Material.TOTEM_OF_UNDYING) {
            data.setLastTotemEquipTime(System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onGuiClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        String title = PlainTextComponentSerializer.plainText().serialize(e.getView().title());
        if (title.contains("Suspicious Players")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            ItemMeta meta = e.getCurrentItem().getItemMeta();
            if (meta == null || meta.displayName() == null) return;
            String rawName = PlainTextComponentSerializer.plainText()
                    .serialize(meta.displayName())
                    .replace(" ⚠ TOTEM", "").trim();
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(
                    plugin.tag() + RJ_AC.color("&7Click: &e/rjcheck " + rawName)));
        }
    }
}
