package dev.rjac.commands;

import dev.rjac.RJ_AC;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ClearCommand implements CommandExecutor {
    private final RJ_AC plugin;
    public ClearCommand(RJ_AC plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("rjac.clear")) { send(sender, "&cNo permission."); return true; }
        if (args.length < 1) { send(sender, "&7Usage: &e/rjclear <player>"); return true; }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) { send(sender, "&cPlayer not found."); return true; }
        plugin.getPlayerDataManager().get(target).clearViolations();
        send(sender, "&aCleared violations for &e" + target.getName() + "&7.");
        return true;
    }
    private void send(CommandSender s, String t) {
        s.sendMessage(LegacyComponentSerializer.legacySection().deserialize(plugin.tag() + RJ_AC.color(t)));
    }
}
