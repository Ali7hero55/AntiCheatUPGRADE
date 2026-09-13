package dev.rjac.checks.misc;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class FastBowCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "FastBow";
    public FastBowCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.fastbow.enabled", true); }

    public void onBowShoot(Player player, PlayerData data) {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        long drawStart = data.getBowDrawStart();
        int minTicks = plugin.getConfig().getInt("checks.fastbow.min-draw-ticks", 10);
        if (drawStart > 0 && (now - drawStart) < (minTicks * 50L)) flag(player);
        data.setBowDrawStart(0);
    }
    public void onBowDraw(Player player, PlayerData data) {
        data.setBowDrawStart(System.currentTimeMillis());
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
