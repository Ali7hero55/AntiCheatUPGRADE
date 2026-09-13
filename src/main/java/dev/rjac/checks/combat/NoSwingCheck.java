package dev.rjac.checks.combat;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class NoSwingCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "NoSwing";
    public NoSwingCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.noswing.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        // If attack packet but no arm-animation packet within 100ms
        if (now - data.getLastArmSwingPacket() > 100) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
