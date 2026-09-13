package dev.rjac.commands;

import dev.rjac.RJ_AC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class GuiCommand implements CommandExecutor {
    private final RJ_AC plugin;
    public GuiCommand(RJ_AC plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage(Component.text("Players only.")); return true; }
        if (!player.hasPermission("rjac.gui")) { send(player, "&cNo permission."); return true; }
        plugin.getCheckManager().openSuspiciousGUI(player);
        return true;
    }
    private void send(CommandSender s, String t) {
        s.sendMessage(LegacyComponentSerializer.legacySection().deserialize(plugin.tag() + RJ_AC.color(t)));
    }
}
