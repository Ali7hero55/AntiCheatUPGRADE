package dev.rjac.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import dev.rjac.RJ_AC;
import dev.rjac.data.PlayerData;
import org.bukkit.entity.Player;

/**
 * Packet-level detection using ProtocolLib.
 *
 * Packets monitored:
 *  FLYING / POSITION / POSITION_LOOK → Timer (packet rate), Blink (position jumps)
 *  ARM_ANIMATION                     → NoSwing (swing without attack packet)
 *  USE_ENTITY                        → Reach (server-side distance validation)
 *  ABILITIES                         → BadPackets (fly flag abuse)
 *  BLOCK_DIG / BLOCK_PLACE           → BadPackets (impossible dig states)
 *  WINDOW_CLICK                      → AutoTotem (fast slot equip)
 */
public class PacketHandler {

    private final RJ_AC plugin;
    private ProtocolManager pm;
    private PacketAdapter adapter;

    public PacketHandler(RJ_AC plugin) {
        this.plugin = plugin;
    }

    public void register() {
        pm = ProtocolLibrary.getProtocolManager();

        adapter = new PacketAdapter(plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.FLYING,
                PacketType.Play.Client.POSITION,
                PacketType.Play.Client.POSITION_LOOK,
                PacketType.Play.Client.LOOK,
                PacketType.Play.Client.ARM_ANIMATION,
                PacketType.Play.Client.USE_ENTITY,
                PacketType.Play.Client.ABILITIES,
                PacketType.Play.Client.BLOCK_DIG,
                PacketType.Play.Client.BLOCK_PLACE,
                PacketType.Play.Client.WINDOW_CLICK) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null || !player.isOnline()) return;
                if (player.hasPermission("rjac.bypass")) return;

                PlayerData data = plugin.getPlayerDataManager().get(player);
                PacketType type = event.getPacketType();

                // ── Timer / Blink detection ──────────────────────────
                if (type == PacketType.Play.Client.FLYING
                        || type == PacketType.Play.Client.POSITION
                        || type == PacketType.Play.Client.POSITION_LOOK
                        || type == PacketType.Play.Client.LOOK) {
                    handleMovementPacket(player, data, event);
                }

                // ── NoSwing (arm animation) ──────────────────────────
                if (type == PacketType.Play.Client.ARM_ANIMATION) {
                    data.setLastArmSwingPacket(System.currentTimeMillis());
                }

                // ── Reach (use-entity distance check) ────────────────
                if (type == PacketType.Play.Client.USE_ENTITY) {
                    handleUseEntity(player, data, event);
                }

                // ── BadPackets — abilities ────────────────────────────
                if (type == PacketType.Play.Client.ABILITIES) {
                    handleAbilities(player, data, event);
                }

                // ── BadPackets — block dig ────────────────────────────
                if (type == PacketType.Play.Client.BLOCK_DIG) {
                    handleBlockDig(player, data, event);
                }

                // ── AutoTotem — window click (slot swap speed) ────────
                if (type == PacketType.Play.Client.WINDOW_CLICK) {
                    handleWindowClick(player, data, event);
                }
            }
        };

        pm.addPacketListener(adapter);
        plugin.getLogger().info("[PacketHandler] ProtocolLib packet listener registered.");
    }

    public void unregister() {
        if (pm != null && adapter != null) {
            pm.removePacketListener(adapter);
        }
    }

    // ── Handlers ──────────────────────────────────────────────────

    private void handleMovementPacket(Player player, PlayerData data, PacketEvent event) {
        long now = System.currentTimeMillis();
        long second = data.getPacketSecond();

        // Reset counter each second
        if (now - second >= 1000) {
            data.setPacketCount(0);
            data.setPacketSecond(now);
        }

        int count = data.getPacketCount() + 1;
        data.setPacketCount(count);

        // Timer: more than max packets/s = timer hack
        if (!plugin.getConfig().getBoolean("checks.timer.enabled", true)) return;
        int maxPps = plugin.getConfig().getInt("checks.timer.max-packets-per-second", 22);
        if (count > maxPps) {
            plugin.getViolationManager().flag(player, "Timer");
        }

        // Blink: sudden large teleport without lag compensation
        // We track large gaps (> 20 blocks in one packet) while not in a vehicle
        if (!plugin.getConfig().getBoolean("checks.blink.enabled", true)) return;
        var pos = event.getPacket();
        if (event.getPacketType() == PacketType.Play.Client.POSITION
                || event.getPacketType() == PacketType.Play.Client.POSITION_LOOK) {
            double x = pos.getDoubles().read(0);
            double y = pos.getDoubles().read(1);
            double z = pos.getDoubles().read(2);
            var last = data.getLastLocation();
            if (last != null && !player.isInsideVehicle()) {
                double dx = x - last.getX();
                double dy = y - last.getY();
                double dz = z - last.getZ();
                double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
                if (dist > 20 && now - data.getLastMoveTime() < 100) {
                    plugin.getViolationManager().flag(player, "Blink");
                }
            }
            data.setLastMoveTime(now);
        }
    }

    private void handleUseEntity(Player player, PlayerData data, PacketEvent event) {
        if (!plugin.getConfig().getBoolean("checks.reach.enabled", true)) return;
        // Get target entity id from packet
        int entityId = event.getPacket().getIntegers().read(0);
        org.bukkit.entity.Entity target = null;
        for (org.bukkit.entity.Entity e : player.getWorld().getEntities()) {
            if (e.getEntityId() == entityId) { target = e; break; }
        }
        if (target == null) return;
        double dist = player.getLocation().distance(target.getLocation());
        double maxReach = plugin.getConfig().getDouble("checks.reach.max-reach", 3.2);
        if (dist > maxReach + 0.5) {
            plugin.getViolationManager().flag(player, "Reach");
        }
    }

    private void handleAbilities(Player player, PlayerData data, PacketEvent event) {
        if (!plugin.getConfig().getBoolean("checks.badpackets.enabled", true)) return;
        // If player sends fly=true while flight is not allowed
        try {
            boolean flying = event.getPacket().getBooleans().read(1);
            if (flying && !player.getAllowFlight() && !player.isOp()) {
                plugin.getViolationManager().flag(player, "BadPackets");
            }
        } catch (Exception ignored) {}
    }

    private void handleBlockDig(Player player, PlayerData data, PacketEvent event) {
        if (!plugin.getConfig().getBoolean("checks.badpackets.enabled", true)) return;
        // Status 0 = started, 2 = finished instantly — flag if same tick
        try {
            int status = event.getPacket().getIntegers().read(0);
            if (status == 2) {
                long now = System.currentTimeMillis();
                if (now - data.getLastMoveTime() < 50) {
                    plugin.getViolationManager().flag(player, "BadPackets");
                }
            }
        } catch (Exception ignored) {}
    }

    private void handleWindowClick(Player player, PlayerData data, PacketEvent event) {
        if (!plugin.getConfig().getBoolean("checks.autototem.enabled", true)) return;
        // Detect rapid slot switch to off-hand (slot 45 = offhand in inventory)
        try {
            int slot = event.getPacket().getIntegers().read(1);
            if (slot == 45) {
                long now = System.currentTimeMillis();
                long last = data.getLastTotemEquipTime();
                data.setLastTotemEquipTime(now);
                long threshold = plugin.getConfig().getLong("checks.autototem.reequip-threshold-ms", 100);
                if (last > 0 && now - last < threshold) {
                    // Instant totem re-equip — flag immediately → autoban
                    plugin.getViolationManager().flag(player, "AutoTotem");
                }
            }
        } catch (Exception ignored) {}
    }
}
