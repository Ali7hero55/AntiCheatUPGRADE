package dev.rjac.command;

import dev.rjac.RJ_AC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class KickCommand implements CommandExecutor, TabCompleter {

    private final RJ_AC plugin;

    public KickCommand(RJ_AC plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("rjac.kick")) {
            sender.sendMessage(Component.text(RJ_AC.color(plugin.getPrefix() + " &cNo permission.")));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(Component.text(RJ_AC.color(plugin.getPrefix() + " &eUsage: /rjkick <player> [reason]")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(Component.text(RJ_AC.color(plugin.getPrefix() + " &cPlayer not found.")));
            return true;
        }

        StringBuilder reason = new StringBuilder();
        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                if (i > 1) reason.append(" ");
                reason.append(args[i]);
            }
        } else {
            reason.append("Kicked by anticheat");
        }

        String reasonStr = reason.toString();
        target.kick(Component.text(RJ_AC.color("&c" + plugin.getPrefix() + "\n\n&eReason: &f" + reasonStr)));

        sender.sendMessage(Component.text(RJ_AC.color(
                plugin.getPrefix() + " &aKicked &e" + target.getName() + " &7(" + reasonStr + ")")));

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("rjac.alerts"))
                .forEach(p -> p.sendMessage(Component.text(RJ_AC.color(
                        plugin.getPrefix() + " &7[KICK] &e" + target.getName()
                                + " &7was kicked by &f" + sender.getName()
                                + " &7- " + reasonStr))));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
