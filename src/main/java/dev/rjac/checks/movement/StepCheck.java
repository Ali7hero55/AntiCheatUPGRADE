package dev.rjac.checks.movement;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

public class StepCheck {
    private final RJ_AC plugin;
    private static final String CHECK = "Step";
    public StepCheck(RJ_AC plugin) { this.plugin = plugin; }
    public boolean isEnabled() { return plugin.getConfig().getBoolean("checks.step.enabled", true); }

    public void check(Player player, PlayerData data) {
        if (!isEnabled()) return;
        if (player.isFlying() || player.isGliding() || player.isInsideVehicle()) return;
        double dy = player.getLocation().getY() - (data.getLastLocation() != null ? data.getLastLocation().getY() : player.getLocation().getY());
        double maxStep = plugin.getConfig().getDouble("checks.step.max-step-height", 1.0);
        if (dy > maxStep && data.isOnGround()) flag(player);
    }
    private void flag(Player p) { plugin.getViolationManager().flag(p, CHECK); }
}
