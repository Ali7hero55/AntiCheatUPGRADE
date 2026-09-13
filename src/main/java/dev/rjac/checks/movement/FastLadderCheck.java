package dev.rjac.checks.movement;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FastLadderCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "FastLadder";
    public FastLadderCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.fastladder.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        Material block = player.getLocation().getBlock().getType();
        if (block != Material.LADDER && block != Material.VINE) return;
        var last = data.getLastLocation();
        if (last == null) return;
        double dy = Math.abs(player.getLocation().getY() - last.getY());
        if (dy > 0.35) flag(player); // vanilla max climb ~0.117/tick
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
