package dev.rjac.checks.movement;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PhaseCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "Phase";
    public PhaseCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.phase.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        Location loc = player.getLocation();
        Material block = loc.getBlock().getType();
        // Inside a solid block they shouldn't pass through
        if (block.isSolid() && !player.isInsideVehicle() && !player.hasPotionEffect(org.bukkit.potion.PotionEffectType.SLOW_FALLING)) {
            flag(player);
        }
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
