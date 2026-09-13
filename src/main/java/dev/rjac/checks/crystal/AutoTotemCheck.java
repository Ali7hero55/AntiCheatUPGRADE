package dev.rjac.checks.crystal;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class AutoTotemCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "AutoTotem";
    public AutoTotemCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.autototem.enabled", true); }

    /** Called on EntityResurrectEvent (totem consumed). */
    public void onTotemUse(Player player, PlayerData data) {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        long lastUse  = data.getLastTotemUseTime();
        long lastEquip = data.getLastTotemEquipTime();
        data.setLastTotemUseTime(now);

        long threshold = plugin.getConfig().getLong("checks.autototem.reequip-threshold-ms", 100);
        // Re-equipped totem before threshold ms since last use → instant flag
        if (lastUse > 0 && now - lastEquip < threshold) {
            plugin.getViolationManager().flag(player, CHECK);
        }
    }
}
