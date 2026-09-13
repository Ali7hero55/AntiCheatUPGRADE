package dev.rjac.commands;

import dev.rjac.RJ_AC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class AlertsCommand implements CommandExecutor {
    private final RJ_AC plugin;
    public AlertsCommand(RJ_AC plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage(Component.text("Players only.")); return true; }
        if (!player.hasPermission("rjac.alerts")) { send(player, "&cNo permission."); return true; }
        plugin.getAlertManager().toggleAlerts(player);
        return true;
    }
    private void send(CommandSender s, String t) {
        s.sendMessage(LegacyComponentSerializer.legacySection().deserialize(plugin.tag() + RJ_AC.color(t)));
    }
}
