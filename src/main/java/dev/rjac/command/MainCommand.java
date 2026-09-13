package dev.rjac.command;

import dev.rjac.RJ_AC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Single entry-point command: /rjac <subcommand> [args...]
 * Alias: /rjanticheat
 *
 * Subcommands:
 *   ban, unban, kick, mute, unmute, warn, check, alerts, gui, clear, reload, info, help
 */
public class MainCommand implements CommandExecutor, TabCompleter {

    private final RJ_AC plugin;

    private static final List<String> SUBS = List.of(
            "ban", "unban", "kick", "mute", "unmute",
            "warn", "check", "alerts", "gui", "clear",
            "reload", "info", "help"
    );

    public MainCommand(RJ_AC plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        // remaining args after the subcommand
        String[] rest = Arrays.copyOfRange(args, 1, args.length);

        switch (sub) {

            // ── /rjac ban <player> <duration> [reason] ──────────────
            case "ban" -> {
                if (!sender.hasPermission("rjac.ban")) { noPerms(sender, "rjac.ban"); return true; }
                if (rest.length < 2) { usage(sender, "ban <player> <duration> [reason]"); return true; }
                Player target = Bukkit.getPlayer(rest[0]);
                if (target == null) { notFound(sender); return true; }
                StringBuilder reason = new StringBuilder();
                for (int i = 2; i < rest.length; i++) { if (i > 2) reason.append(" "); reason.append(rest[i]); }
                String reasonStr = reason.length() > 0 ? reason.toString() : "Banned by staff";
                plugin.getPunishmentManager().ban(target, rest[1], reasonStr, sender);
            }

            // ── /rjac unban <player> ────────────────────────────────
            case "unban" -> {
                if (!sender.hasPermission("rjac.unban")) { noPerms(sender, "rjac.unban"); return true; }
                if (rest.length < 1) { usage(sender, "unban <player>"); return true; }
                plugin.getPunishmentManager().unban(rest[0], sender);
            }

            // ── /rjac kick <player> [reason] ───────────────────────
            case "kick" -> {
                if (!sender.hasPermission("rjac.kick")) { noPerms(sender, "rjac.kick"); return true; }
                if (rest.length < 1) { usage(sender, "kick <player> [reason]"); return true; }
                Player target = Bukkit.getPlayer(rest[0]);
                if (target == null) { notFound(sender); return true; }
                StringBuilder reason = new StringBuilder();
                for (int i = 1; i < rest.length; i++) { if (i > 1) reason.append(" "); reason.append(rest[i]); }
                String reasonStr = reason.length() > 0 ? reason.toString() : "Kicked by staff";
                target.kick(Component.text(RJ_AC.color(
                        "&c" + plugin.getPrefix() + "\n\n&eReason: &f" + reasonStr)));
                broadcast(sender, "&aKicked &e" + target.getName() + " &7- " + reasonStr);
            }

            // ── /rjac mute <player> <duration> [reason] ────────────
            case "mute" -> {
                if (!sender.hasPermission("rjac.mute")) { noPerms(sender, "rjac.mute"); return true; }
                if (rest.length < 2) { usage(sender, "mute <player> <duration> [reason]"); return true; }
                Player target = Bukkit.getPlayer(rest[0]);
                if (target == null) { notFound(sender); return true; }
                StringBuilder reason = new StringBuilder();
                for (int i = 2; i < rest.length; i++) { if (i > 2) reason.append(" "); reason.append(rest[i]); }
                String reasonStr = reason.length() > 0 ? reason.toString() : "Muted by staff";
                plugin.getMuteManager().mute(target, rest[1], reasonStr, sender);
            }

            // ── /rjac unmute <player> ───────────────────────────────
            case "unmute" -> {
                if (!sender.hasPermission("rjac.unmute")) { noPerms(sender, "rjac.unmute"); return true; }
                if (rest.length < 1) { usage(sender, "unmute <player>"); return true; }
                plugin.getMuteManager().unmute(rest[0], sender);
            }

            // ── /rjac warn <player> [reason] ───────────────────────
            case "warn" -> {
                if (!sender.hasPermission("rjac.warn")) { noPerms(sender, "rjac.warn"); return true; }
                if (rest.length < 1) { usage(sender, "warn <player> [reason]"); return true; }
                Player target = Bukkit.getPlayer(rest[0]);
                if (target == null) { notFound(sender); return true; }
                StringBuilder reason = new StringBuilder();
                for (int i = 1; i < rest.length; i++) { if (i > 1) reason.append(" "); reason.append(rest[i]); }
                String reasonStr = reason.length() > 0 ? reason.toString() : "Warned by staff";
                target.sendMessage(Component.text(RJ_AC.color(
                        plugin.getPrefix() + " &eYou have been warned: &f" + reasonStr)));
                broadcast(sender, "&eWarned &f" + target.getName() + " &7- " + reasonStr);
            }

            // ── /rjac check <player> ────────────────────────────────
            case "check" -> {
                if (!sender.hasPermission("rjac.check")) { noPerms(sender, "rjac.check"); return true; }
                if (rest.length < 1) { usage(sender, "check <player>"); return true; }
                Player target = Bukkit.getPlayer(rest[0]);
                if (target == null) { notFound(sender); return true; }
                var data = plugin.getPlayerDataManager().get(target);
                var violations = data.getAllViolations();
                if (violations.isEmpty()) {
                    msg(sender, "&aNo violations for &e" + target.getName());
                } else {
                    msg(sender, "&eViolations for &f" + target.getName() + "&e:");
                    violations.forEach((check, vl) ->
                            msg(sender, "  &7» &b" + check + " &7— VL:&c" + vl));
                }
            }

            // ── /rjac alerts ────────────────────────────────────────
            case "alerts" -> {
                if (!(sender instanceof Player p)) { msg(sender, "&cOnly players can toggle alerts."); return true; }
                if (!sender.hasPermission("rjac.alerts")) { noPerms(sender, "rjac.alerts"); return true; }
                plugin.getAlertManager().toggleAlerts(p);
            }

            // ── /rjac gui ───────────────────────────────────────────
            case "gui" -> {
                if (!(sender instanceof Player p)) { msg(sender, "&cOnly players can open the GUI."); return true; }
                if (!sender.hasPermission("rjac.gui")) { noPerms(sender, "rjac.gui"); return true; }
                plugin.getCheckManager().openSuspiciousGUI(p);
            }

            // ── /rjac clear <player> ────────────────────────────────
            case "clear" -> {
                if (!sender.hasPermission("rjac.clear")) { noPerms(sender, "rjac.clear"); return true; }
                if (rest.length < 1) { usage(sender, "clear <player>"); return true; }
                Player target = Bukkit.getPlayer(rest[0]);
                if (target == null) { notFound(sender); return true; }
                plugin.getPlayerDataManager().get(target).clearViolations();
                msg(sender, "&aCleared violations for &e" + target.getName());
            }

            // ── /rjac reload ────────────────────────────────────────
            case "reload" -> {
                if (!sender.hasPermission("rjac.reload")) { noPerms(sender, "rjac.reload"); return true; }
                plugin.reloadConfig();
                msg(sender, "&aConfig reloaded.");
            }

            // ── /rjac info ──────────────────────────────────────────
            case "info" -> {
                sender.sendMessage(Component.text(""));
                sender.sendMessage(Component.text(RJ_AC.color(plugin.getPrefix()))
                        .append(Component.text(" — Advanced Anticheat", NamedTextColor.GRAY)));
                sender.sendMessage(Component.text(RJ_AC.color("&7Author: &fRJ_79")));
                sender.sendMessage(Component.text(RJ_AC.color("&7Version: &f" + plugin.getDescription().getVersion())));
                sender.sendMessage(Component.text(RJ_AC.color("&7Checks: &f29  &7| ProtocolLib packet detection enabled")));
                sender.sendMessage(Component.text(""));
            }

            // ── /rjac help ──────────────────────────────────────────
            case "help" -> sendHelp(sender);

            default -> {
                msg(sender, "&cUnknown subcommand. Use &e/rjac help");
            }
        }
        return true;
    }

    // ── Tab completion ────────────────────────────────────────────

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return SUBS.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            boolean needsPlayer = List.of("ban","kick","mute","unmute","warn","check","clear","unban").contains(sub);
            if (needsPlayer) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (List.of("ban","mute").contains(sub)) {
                return List.of("1d","7d","30d","perm");
            }
        }
        return List.of();
    }

    // ── Helpers ───────────────────────────────────────────────────

    private void sendHelp(CommandSender sender) {
        String pfx = plugin.getPrefix();
        sender.sendMessage(Component.text(""));
        sender.sendMessage(Component.text(RJ_AC.color(pfx + " &7— Command Help"), NamedTextColor.WHITE)
                .decorate(TextDecoration.BOLD));
        // {subcommand+args, description, permission}
        String[][] cmds = {
                {"ban <player> <duration> [reason]",  "Ban a player",          "rjac.ban"},
                {"unban <player>",                    "Unban a player",         "rjac.unban"},
                {"kick <player> [reason]",            "Kick a player",          "rjac.kick"},
                {"mute <player> <duration> [reason]", "Mute a player",          "rjac.mute"},
                {"unmute <player>",                   "Unmute a player",        "rjac.unmute"},
                {"warn <player> [reason]",            "Warn a player",          "rjac.warn"},
                {"check <player>",                    "View violations",        "rjac.check"},
                {"alerts",                            "Toggle staff alerts",    "rjac.alerts"},
                {"gui",                               "Open suspicious GUI",    "rjac.gui"},
                {"clear <player>",                    "Clear violations",       "rjac.clear"},
                {"reload",                            "Reload config",          "rjac.reload"},
                {"info",                              "Plugin info",            "rjac.use"},
        };
        for (String[] c : cmds) {
            boolean hasPerm = sender.hasPermission(c[2]);
            String permColor = hasPerm ? "&a" : "&c";
            sender.sendMessage(Component.text(RJ_AC.color(
                    "  &8/rjac &b" + c[0] + " &8— &7" + c[1]
                    + "  " + permColor + c[2])));
        }
        sender.sendMessage(Component.text(RJ_AC.color("  &8Alias: &7/rjanticheat")));
        sender.sendMessage(Component.text(""));
    }

    private void msg(CommandSender s, String text) {
        s.sendMessage(Component.text(RJ_AC.color(plugin.getPrefix() + " " + text)));
    }

    private void noPerms(CommandSender s, String permission) {
        msg(s, "&cNo permission. &7Required: &f" + permission);
    }

    private void notFound(CommandSender s) {
        msg(s, "&cPlayer not found or not online.");
    }

    private void usage(CommandSender s, String usage) {
        msg(s, "&eUsage: &f/rjac " + usage);
    }

    private void broadcast(CommandSender sender, String text) {
        String full = plugin.getPrefix() + " " + text;
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("rjac.alerts"))
                .forEach(p -> p.sendMessage(Component.text(RJ_AC.color(full))));
        sender.sendMessage(Component.text(RJ_AC.color(full)));
    }
}
