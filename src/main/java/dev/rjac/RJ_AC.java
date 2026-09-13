package dev.rjac;

import dev.rjac.command.MainCommand;
import dev.rjac.listeners.*;
import dev.rjac.manager.*;
import dev.rjac.packet.PacketHandler;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

public class RJ_AC extends JavaPlugin {

    private static RJ_AC instance;

    private PlayerDataManager playerDataManager;
    private ViolationManager  violationManager;
    private PunishmentManager punishmentManager;
    private AlertManager      alertManager;
    private MuteManager       muteManager;
    private CheckManager      checkManager;
    private PacketHandler     packetHandler;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        playerDataManager = new PlayerDataManager(this);
        alertManager      = new AlertManager(this);
        punishmentManager = new PunishmentManager(this);
        violationManager  = new ViolationManager(this);
        muteManager       = new MuteManager(this);
        checkManager      = new CheckManager(this);

        getServer().getPluginManager().registerEvents(new CombatListener(this),    this);
        getServer().getPluginManager().registerEvents(new MovementListener(this),  this);
        getServer().getPluginManager().registerEvents(new CrystalListener(this),   this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this),    this);
        getServer().getPluginManager().registerEvents(new ChatListener(this),      this);

        packetHandler = new PacketHandler(this);
        packetHandler.register();

        MainCommand mainCmd = new MainCommand(this);
        getCommand("rjac").setExecutor(mainCmd);
        getCommand("rjac").setTabCompleter(mainCmd);

        getLogger().info(color(getPrefix()) + " Enabled — by RJ_79 [Paper 1.21.11]");
    }

    @Override
    public void onDisable() {
        if (packetHandler != null) packetHandler.unregister();
        getLogger().info(color(getPrefix()) + " Disabled.");
    }

    public static RJ_AC getInstance()               { return instance; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public ViolationManager  getViolationManager()  { return violationManager; }
    public PunishmentManager getPunishmentManager() { return punishmentManager; }
    public AlertManager      getAlertManager()      { return alertManager; }
    public MuteManager       getMuteManager()       { return muteManager; }
    public CheckManager      getCheckManager()      { return checkManager; }
    public PacketHandler     getPacketHandler()     { return packetHandler; }

    /** Returns the raw prefix string from config (uses & codes). */
    public String getPrefix() {
        return getConfig().getString("prefix", "&4RJ&7_&bAnti&1CHEAT");
    }

    /**
     * Translates &-colour codes to §-coded string.
     * Safe for logger output and for wrapping with LegacyComponentSerializer.
     */
    public static String color(String s) {
        return LegacyComponentSerializer.legacyAmpersand()
                .serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(s));
    }

    /**
     * Returns a §-coded prefix + grey space, used as a tag prefix
     * before wrapping the whole string in a Component.
     */
    public String tag() {
        return color(getPrefix()) + "§7 ";
    }
}
