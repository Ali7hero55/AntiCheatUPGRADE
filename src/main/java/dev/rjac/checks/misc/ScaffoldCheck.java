package dev.rjac.checks.misc;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class ScaffoldCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "Scaffold";
    public ScaffoldCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.scaffold.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        if (now - data.getLastBlockPlaceTime() < 50) { // placing > 20 blocks/s
            data.setScaffoldCount(data.getScaffoldCount() + 1);
            if (data.getScaffoldCount() > 10) flag(player);
        } else {
            data.setScaffoldCount(0);
        }
        data.setLastBlockPlaceTime(now);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
