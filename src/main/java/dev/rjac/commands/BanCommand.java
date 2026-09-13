package dev.rjac.commands;

import dev.rjac.RJ_AC;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.Arrays;

public class BanCommand implements CommandExecutor {
    private final RJ_AC plugin;
    public BanCommand(RJ_AC plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("rjac.ban")) { send(sender, "&cNo permission."); return true; }
        if (args.length < 2) { send(sender, "&7Usage: &e/rjban <player> <duration> [reason]"); return true; }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) { send(sender, "&cPlayer not found."); return true; }
        String duration = args[1];
        String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "Anticheat";
        plugin.getPunishmentManager().ban(target, duration, reason, sender);
        send(sender, "&aBanned &e" + target.getName() + " &7for &f" + duration + "&7.");
        return true;
    }
    private void send(CommandSender s, String t) {
        s.sendMessage(LegacyComponentSerializer.legacySection().deserialize(plugin.tag() + RJ_AC.color(t)));
    }
}
