package dev.rjac.checks.combat;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class AutoClickerCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "AutoClicker";
    public AutoClickerCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.autoclicker.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        if (now - data.getCpsSecond() >= 1000) {
            data.setCpsSecond(now);
            data.setClicksThisSecond(0);
        }
        data.setClicksThisSecond(data.getClicksThisSecond() + 1);
        int maxCps = plugin.getConfig().getInt("checks.autoclicker.max-cps", 20);
        if (data.getClicksThisSecond() > maxCps) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
