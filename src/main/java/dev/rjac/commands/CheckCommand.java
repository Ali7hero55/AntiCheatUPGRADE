package dev.rjac.commands;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CheckCommand implements CommandExecutor {
    private final RJ_AC plugin;
    public CheckCommand(RJ_AC plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("rjac.check")) { sender.sendMessage(leg(plugin.tag() + RJ_AC.color("&cNo permission."))); return true; }
        if (args.length < 1) { sender.sendMessage(leg(plugin.tag() + RJ_AC.color("&7Usage: &e/rjcheck <player>"))); return true; }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) { sender.sendMessage(leg(plugin.tag() + RJ_AC.color("&cPlayer not found."))); return true; }
        PlayerData data = plugin.getPlayerDataManager().get(target);
        sender.sendMessage(leg(plugin.tag() + RJ_AC.color("&eViolations for &b" + target.getName() + "&7:")));
        if (data.getAllViolations().isEmpty()) {
            sender.sendMessage(leg(plugin.tag() + RJ_AC.color("&aNone.")));
        } else {
            data.getAllViolations().entrySet().stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .forEach(e -> sender.sendMessage(
                            Component.text("  " + e.getKey() + ": ").color(NamedTextColor.GOLD)
                                    .append(Component.text(e.getValue()).color(NamedTextColor.WHITE))));
        }
        return true;
    }

    private static Component leg(String s) {
        return LegacyComponentSerializer.legacySection().deserialize(s);
    }
}
