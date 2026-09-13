package dev.rjac.listeners;

import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementListener implements Listener {

    private final RJ_AC plugin;
    public MovementListener(RJ_AC plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("rjac.bypass")) return;
        PlayerData data = plugin.getPlayerDataManager().get(player);

        double dy = e.getTo().getY() - e.getFrom().getY();
        data.setOnGround(player.isOnGround());
        data.setLastDeltaY(dy);
        if (!player.isOnGround()) data.setAirTicks(data.getAirTicks() + 1);
        else data.setAirTicks(0);

        // Keep location history (last 20 positions for backtrack)
        var hist = data.getLocationHistory();
        hist.addLast(e.getFrom().clone());
        if (hist.size() > 20) hist.pollFirst();

        var cm = plugin.getCheckManager();
        cm.speed.check(player, data);
        cm.flight.check(player, data);
        cm.noFall.check(player, data);
        cm.blink.check(player, data);
        cm.phase.check(player, data);
        cm.step.check(player, data);
        cm.jesus.check(player, data);
        cm.timer.check(player, data);
        cm.fastLadder.check(player, data);
        cm.inventoryMove.check(player, data);

        data.setLastLocation(e.getTo().clone());
    }
}
