package dev.rjac.gui;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.stream.Collectors;

public class SuspiciousGUI {

    private final RJ_AC plugin;

    public SuspiciousGUI(RJ_AC plugin) { this.plugin = plugin; }

    public void open(Player staff) {
        List<PlayerData> suspects = plugin.getPlayerDataManager().getAll().stream()
                .filter(d -> d.getTotalViolations() > 0)
                .sorted(Comparator.comparingInt(PlayerData::getTotalViolations).reversed())
                .collect(Collectors.toList());

        int size = Math.max(9, Math.min(54, ((suspects.size() / 9) + 1) * 9));
        Inventory inv = Bukkit.createInventory(null, size,
                LegacyComponentSerializer.legacyAmpersand().deserialize(plugin.getPrefix() + " &7\u00bb &bSuspicious Players"));

        for (PlayerData data : suspects) {
            Player target = Bukkit.getPlayer(data.getUuid());
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();

            boolean totemAbuser = data.getViolations("autototem") > 0;
            Component displayName = totemAbuser
                    ? Component.text(data.getName() + " \u26a0 TOTEM").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
                    : Component.text(data.getName()).color(NamedTextColor.YELLOW);
            meta.displayName(displayName);

            if (target != null) meta.setOwningPlayer(target);

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Total VL: ").color(NamedTextColor.GRAY)
                    .append(Component.text(data.getTotalViolations()).color(NamedTextColor.RED)));
            lore.add(Component.empty());
            data.getAllViolations().entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(e -> lore.add(
                            Component.text(e.getKey() + ": ").color(NamedTextColor.GOLD)
                                    .append(Component.text(e.getValue()).color(NamedTextColor.WHITE))));
            if (totemAbuser) {
                lore.add(Component.empty());
                lore.add(Component.text(">> AUTO-TOTEM DETECTED <<").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
            }

            meta.lore(lore);
            skull.setItemMeta(meta);
            inv.addItem(skull);
        }

        if (suspects.isEmpty()) {
            ItemStack glass = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            ItemMeta m = glass.getItemMeta();
            m.displayName(Component.text("No suspicious players found!").color(NamedTextColor.GREEN));
            glass.setItemMeta(m);
            inv.setItem(4, glass);
        }

        staff.openInventory(inv);
    }
}
