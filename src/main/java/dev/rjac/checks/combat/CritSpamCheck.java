package dev.rjac.checks.combat;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class CritSpamCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "CritSpam";
    public CritSpamCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.critspam.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        // Critical hits require player to be falling (deltaY < 0) and not on ground
        if (data.isOnGround() || data.getLastDeltaY() >= 0) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
