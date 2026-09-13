package dev.rjac.commands;

import dev.rjac.RJ_AC;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.Arrays;

public class WarnCommand implements CommandExecutor {
    private final RJ_AC plugin;
    public WarnCommand(RJ_AC plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("rjac.warn")) { send(sender, "&cNo permission."); return true; }
        if (args.length < 1) { send(sender, "&7Usage: &e/rjwarn <player> [reason]"); return true; }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) { send(sender, "&cPlayer not found."); return true; }
        String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "No reason given";
        target.sendMessage(LegacyComponentSerializer.legacySection().deserialize(
                plugin.tag() + RJ_AC.color("&eYou have been warned: &f" + reason)));
        send(sender, "&aWarned &e" + target.getName() + "&7: &f" + reason);
        return true;
    }
    private void send(CommandSender s, String t) {
        s.sendMessage(LegacyComponentSerializer.legacySection().deserialize(plugin.tag() + RJ_AC.color(t)));
    }
}
