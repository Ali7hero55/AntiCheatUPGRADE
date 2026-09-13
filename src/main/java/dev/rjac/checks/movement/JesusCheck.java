package dev.rjac.checks.movement;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class JesusCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "Jesus";
    public JesusCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.jesus.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        if (player.hasPotionEffect(PotionEffectType.WATER_BREATHING) || player.isInsideVehicle()) return;
        Material below = player.getLocation().subtract(0, 0.1, 0).getBlock().getType();
        if ((below == Material.WATER || below == Material.LAVA) && data.isOnGround()) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
