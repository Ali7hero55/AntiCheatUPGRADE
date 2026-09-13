package dev.rjac.manager;

import dev.rjac.RJ_AC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AlertManager {

    private final RJ_AC plugin;
    private final Set<UUID> alertsEnabled = new HashSet<>();

    public AlertManager(RJ_AC plugin) {
        this.plugin = plugin;
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("rjac.alerts"))
                .forEach(p -> alertsEnabled.add(p.getUniqueId()));
    }

    public void toggleAlerts(Player player) {
        if (alertsEnabled.contains(player.getUniqueId())) {
            alertsEnabled.remove(player.getUniqueId());
            player.sendMessage(Component.text(RJ_AC.color(plugin.getPrefix() + " "))
                    .append(Component.text("Alerts ", NamedTextColor.GRAY))
                    .append(Component.text("disabled", NamedTextColor.RED).decorate(TextDecoration.BOLD)));
        } else {
            alertsEnabled.add(player.getUniqueId());
            player.sendMessage(Component.text(RJ_AC.color(plugin.getPrefix() + " "))
                    .append(Component.text("Alerts ", NamedTextColor.GRAY))
                    .append(Component.text("enabled", NamedTextColor.GREEN).decorate(TextDecoration.BOLD)));
        }
    }

    public boolean hasAlerts(Player player) {
        return alertsEnabled.contains(player.getUniqueId());
    }

    /**
     * Sends a staff alert with clickable [BAN] [KICK] [WARN] [CHECK] buttons.
     */
    public void sendAlert(Player flagged, String checkName, int vl) {
        long cooldown = plugin.getConfig().getLong("general.alert-cooldown", 3000L);
        var data = plugin.getPlayerDataManager().get(flagged);
        if (!data.canAlert(checkName, cooldown)) return;

        String playerName = flagged.getName();
        String banDur = plugin.getConfig().getString("checks." + checkName.toLowerCase() + ".ban-duration", "7d");

        // ── Build the alert line ─────────────────────────────────
        // [PREFIX] ⚠ PlayerName failed CheckName  VL:X  [BAN] [KICK] [WARN] [CHECK]
        Component prefix = Component.text(RJ_AC.color(plugin.getPrefix() + " "), NamedTextColor.WHITE);

        Component warning = Component.text("⚠ ", NamedTextColor.GOLD).decorate(TextDecoration.BOLD);

        Component name = Component.text(playerName, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Click to check " + playerName, NamedTextColor.GRAY)))
                .clickEvent(ClickEvent.runCommand("/rjcheck " + playerName));

        Component failed = Component.text(" failed ", NamedTextColor.GRAY);

        Component check = Component.text(checkName, NamedTextColor.AQUA).decorate(TextDecoration.BOLD);

        Component vlText = Component.text("  VL:", NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(vl), vlColor(vl)));

        Component spacer = Component.text("  ", NamedTextColor.GRAY);

        // ── Action buttons ────────────────────────────────────────
        Component banBtn = buildButton(
                "BAN",
                NamedTextColor.RED,
                "/rjban " + playerName + " " + banDur + " Anticheat: " + checkName,
                "Ban " + playerName + " for " + banDur);

        Component kickBtn = buildButton(
                "KICK",
                NamedTextColor.GOLD,
                "/rjkick " + playerName + " Anticheat: " + checkName,
                "Kick " + playerName + " from the server");

        Component warnBtn = buildButton(
                "WARN",
                NamedTextColor.YELLOW,
                "/rjwarn " + playerName + " Anticheat: " + checkName,
                "Send a warning to " + playerName);

        Component checkBtn = buildButton(
                "CHECK",
                NamedTextColor.GREEN,
                "/rjcheck " + playerName,
                "View all violations for " + playerName);

        Component tpBtn = buildButton(
                "TP",
                NamedTextColor.LIGHT_PURPLE,
                "/tp " + playerName,
                "Teleport to " + playerName);

        Component alert = prefix
                .append(warning)
                .append(name)
                .append(failed)
                .append(check)
                .append(vlText)
                .append(spacer)
                .append(banBtn)
                .append(Component.text(" "))
                .append(kickBtn)
                .append(Component.text(" "))
                .append(warnBtn)
                .append(Component.text(" "))
                .append(checkBtn)
                .append(Component.text(" "))
                .append(tpBtn);

        // ── Send to all staff with alerts on ──────────────────────
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (alertsEnabled.contains(staff.getUniqueId()) && staff.hasPermission("rjac.alerts")) {
                staff.sendMessage(alert);
            }
        }

        if (plugin.getConfig().getBoolean("general.log-to-file", true)) {
            plugin.getLogger().info("[ALERT] " + playerName + " | " + checkName + " | VL:" + vl);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────

    /** Build a hoverable, clickable [LABEL] button. */
    private Component buildButton(String label, NamedTextColor color, String command, String hoverText) {
        return Component.text("[" + label + "]", color)
                .decorate(TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(
                        Component.text(hoverText, NamedTextColor.GRAY)
                                .append(Component.newline())
                                .append(Component.text("Click to run: ", NamedTextColor.DARK_GRAY))
                                .append(Component.text(command, NamedTextColor.WHITE))))
                .clickEvent(ClickEvent.runCommand(command));
    }

    /** Color VL number by severity. */
    private NamedTextColor vlColor(int vl) {
        if (vl >= 15) return NamedTextColor.DARK_RED;
        if (vl >= 8)  return NamedTextColor.RED;
        if (vl >= 4)  return NamedTextColor.GOLD;
        return NamedTextColor.YELLOW;
    }

    public void playerJoin(Player player) {
        if (player.hasPermission("rjac.alerts")) alertsEnabled.add(player.getUniqueId());
    }

    public void playerQuit(Player player) {
        alertsEnabled.remove(player.getUniqueId());
    }
}
