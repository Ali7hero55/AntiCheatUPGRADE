package dev.rjac.listeners;

import dev.rjac.RJ_AC;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@SuppressWarnings("deprecation")
public class ChatListener implements Listener {

    private final RJ_AC plugin;
    public ChatListener(RJ_AC plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (plugin.getMuteManager().isMuted(player)) {
            e.setCancelled(true);
            String msg = plugin.tag() + RJ_AC.color(
                    plugin.getConfig().getString("punishments.mute-message",
                            "&cYou are muted. Reason: &f{reason}")
                            .replace("{reason}", plugin.getMuteManager().getReason(player.getUniqueId())));
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(msg));
        }
    }
}
