package dev.rjac.checks.misc;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class InventoryMoveCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "InventoryMove";
    public InventoryMoveCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.inventorymove.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        if (!data.isInventoryOpen()) return;
        var last = data.getLastLocation();
        if (last == null) return;
        double dx = player.getLocation().getX() - last.getX();
        double dz = player.getLocation().getZ() - last.getZ();
        if (Math.sqrt(dx*dx + dz*dz) > 0.05) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
