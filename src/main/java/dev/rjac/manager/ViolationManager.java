package dev.rjac.manager;

import dev.rjac.RJ_AC;
import org.bukkit.entity.Player;

public class ViolationManager {

    private final RJ_AC plugin;

    public ViolationManager(RJ_AC plugin) {
        this.plugin = plugin;
    }

    /**
     * Called by every check when a violation is detected.
     *
     * @param player    The flagged player
     * @param checkName Internal check name matching the config key (e.g. "killaura")
     * @param weight    How many VL points this violation adds (usually 1)
     */
    public void flag(Player player, String checkName, int weight) {
        var data = plugin.getPlayerDataManager().get(player);
        data.addViolation(checkName, weight);
        int vl = data.getViolations(checkName);

        // ── Staff alert with clickable actions ──
        if (plugin.getConfig().getBoolean("checks." + checkName.toLowerCase() + ".warn-staff", true)) {
            plugin.getAlertManager().sendAlert(player, checkName, vl);
        }

        // ── Auto-ban if threshold reached ──
        boolean autoBan = plugin.getConfig().getBoolean("checks." + checkName.toLowerCase() + ".auto-ban", false);
        int banThreshold = plugin.getConfig().getInt("general.ban-threshold", 20);

        if (autoBan && vl >= banThreshold) {
            String duration = plugin.getConfig().getString("checks." + checkName.toLowerCase() + ".ban-duration", "7d");
            String reason = "Anticheat: " + checkName;
            plugin.getPunishmentManager().ban(player, duration, reason, null);
        }
    }

    /**
     * Convenience: flag with a weight of 1.
     */
    public void flag(Player player, String checkName) {
        flag(player, checkName, 1);
    }
}
