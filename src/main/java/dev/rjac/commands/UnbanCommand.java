package dev.rjac.commands;

import dev.rjac.RJ_AC;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.*;

public class UnbanCommand implements CommandExecutor {
    private final RJ_AC plugin;
    public UnbanCommand(RJ_AC plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("rjac.ban")) { send(sender, "&cNo permission."); return true; }
        if (args.length < 1) { send(sender, "&7Usage: &e/rjunban <player>"); return true; }
        plugin.getPunishmentManager().unban(args[0]);
        send(sender, "&aUnbanned &e" + args[0] + "&7.");
        return true;
    }
    private void send(CommandSender s, String t) {
        s.sendMessage(LegacyComponentSerializer.legacySection().deserialize(plugin.tag() + RJ_AC.color(t)));
    }
}
