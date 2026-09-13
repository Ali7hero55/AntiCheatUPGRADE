package dev.rjac.commands;

import dev.rjac.RJ_AC;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class UnmuteCommand implements CommandExecutor {
    private final RJ_AC plugin;
    public UnmuteCommand(RJ_AC plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("rjac.mute")) { send(sender, "&cNo permission."); return true; }
        if (args.length < 1) { send(sender, "&7Usage: &e/rjunmute <player>"); return true; }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) { send(sender, "&cPlayer not found."); return true; }
        plugin.getMuteManager().unmute(target.getUniqueId());
        send(sender, "&aUnmuted &e" + target.getName() + "&7.");
        return true;
    }
    private void send(CommandSender s, String t) {
        s.sendMessage(LegacyComponentSerializer.legacySection().deserialize(plugin.tag() + RJ_AC.color(t)));
    }
}
