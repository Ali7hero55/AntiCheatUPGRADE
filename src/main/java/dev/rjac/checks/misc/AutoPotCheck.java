package dev.rjac.checks.misc;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class AutoPotCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "AutoPot";
    public AutoPotCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.autopot.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        long minInterval = plugin.getConfig().getLong("checks.autopot.min-throw-interval-ms", 200);
        if (data.getLastPotionThrowTime() > 0 && now - data.getLastPotionThrowTime() < minInterval) flag(player);
        data.setLastPotionThrowTime(now);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
