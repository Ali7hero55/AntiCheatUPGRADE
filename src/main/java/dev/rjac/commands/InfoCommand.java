package dev.rjac.commands;

import dev.rjac.RJ_AC;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.*;

public class InfoCommand implements CommandExecutor {
    private final RJ_AC plugin;
    public InfoCommand(RJ_AC plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        send(sender, " &7v" + plugin.getDescription().getVersion() + " &8| &7by &bRJ_79");
        send(sender, "  &7Checks: &e29 &7| &7ProtocolLib packet detection enabled");
        return true;
    }
    private void send(CommandSender s, String t) {
        s.sendMessage(LegacyComponentSerializer.legacySection().deserialize(plugin.tag() + RJ_AC.color(t)));
    }
}
