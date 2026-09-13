package dev.rjac.manager;

import dev.rjac.RJ_AC;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class PunishmentManager {

    private final RJ_AC plugin;

    public PunishmentManager(RJ_AC plugin) {
        this.plugin = plugin;
    }

    public void ban(Player player, String reason, String duration) {
        Date expiry = parseDuration(duration);
        String display = plugin.tag() + RJ_AC.color(
                plugin.getConfig().getString("punishments.ban-message",
                        "&cYou have been banned.\n&7Reason: &f{reason}\n&7Duration: &f{duration}")
                        .replace("{reason}", reason)
                        .replace("{duration}", duration));

        Bukkit.getBanList(BanList.Type.NAME)
                .addBan(player.getName(), reason, expiry, "RJ_AC");
        player.kickPlayer(RJ_AC.color(display));
        plugin.getLogger().info("[BAN] " + player.getName() + " | " + reason + " | " + duration);
    }

    public void kick(Player player, String reason) {
        String msg = plugin.tag() + RJ_AC.color(
                plugin.getConfig().getString("punishments.kick-message",
                        "&cYou were kicked by RJ_AC.\n&7Reason: &f{reason}")
                        .replace("{reason}", reason));
        player.kickPlayer(msg);
    }

    public void unban(String name) {
        Bukkit.getBanList(BanList.Type.NAME).pardon(name);
    }

    // ── Duration parser ────────────────────────────────────────────
    // Formats: permanent / perm / forever → null (permanent)
    // Otherwise: 1d, 2h, 30m, 1d12h, etc.
    public Date parseDuration(String input) {
        if (input == null) return null;
        String lower = input.trim().toLowerCase();
        if (lower.equals("permanent") || lower.equals("perm") || lower.equals("forever")) return null;

        long totalSeconds = 0;
        StringBuilder num = new StringBuilder();
        for (char c : lower.toCharArray()) {
            if (Character.isDigit(c)) {
                num.append(c);
            } else {
                if (num.length() == 0) continue;
                long val = Long.parseLong(num.toString());
                num.setLength(0);
                switch (c) {
                    case 's' -> totalSeconds += val;
                    case 'm' -> totalSeconds += val * 60;
                    case 'h' -> totalSeconds += val * 3600;
                    case 'd' -> totalSeconds += val * 86400;
                    case 'w' -> totalSeconds += val * 604800;
                }
            }
        }
        if (totalSeconds == 0) return null;
        return Date.from(Instant.now().plus(Duration.ofSeconds(totalSeconds)));
    }

    public String formatDuration(String input) {
        if (input == null) return "Permanent";
        String lower = input.trim().toLowerCase();
        if (lower.equals("permanent") || lower.equals("perm") || lower.equals("forever")) return "Permanent";
        return input;
    }
}
