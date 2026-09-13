package dev.rjac.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerData {

    private final UUID uuid;
    private final String name;

    // ── Violation tracking ─────────────────────────────────────────
    private final Map<String, Integer> violations = new HashMap<>();

    // ── Packet / Timer ────────────────────────────────────────────
    private long lastPacketTime = System.currentTimeMillis();
    private int  packetCount    = 0;
    private long packetSecond   = System.currentTimeMillis();

    // ── Movement ──────────────────────────────────────────────────
    private Location lastLocation;
    private Location previousLocation;
    private double   lastDeltaY   = 0;
    private boolean  wasOnGround  = true;
    private boolean  isOnGround   = true;
    private int      airTicks     = 0;
    private long     lastMoveTime = System.currentTimeMillis();
    private final Deque<Location> locationHistory = new ArrayDeque<>();

    // ── Combat ────────────────────────────────────────────────────
    private long lastAttackTime    = 0;
    private int  clicksThisSecond  = 0;
    private long cpsSecond         = System.currentTimeMillis();
    private long lastSwingTime     = 0;
    private boolean swungThisTick  = false;
    private long lastDamageTime    = 0;

    // ── Arm swing (packet-detected) ───────────────────────────────
    private long lastArmSwingPacket = 0;

    // ── Reach (packet-detected) ───────────────────────────────────
    private Location lastInteractLocation;

    // ── Crystal PvP ───────────────────────────────────────────────
    private long lastCrystalPlaceTime  = 0;
    private int  crystalPlacesPerSecond = 0;
    private long crystalSecond          = System.currentTimeMillis();
    private long lastCrystalExplodeTime = 0;

    // ── AutoTotem ─────────────────────────────────────────────────
    private long lastTotemUseTime    = 0;
    private long lastTotemEquipTime  = 0;

    // ── Potion ────────────────────────────────────────────────────
    private long lastPotionThrowTime = 0;

    // ── Bow ───────────────────────────────────────────────────────
    private long bowDrawStart = 0;

    // ── Inventory ─────────────────────────────────────────────────
    private boolean inventoryOpen = false;

    // ── Scaffold ──────────────────────────────────────────────────
    private long lastBlockPlaceTime = 0;
    private int  scaffoldCount      = 0;

    // ── Alerts ────────────────────────────────────────────────────
    private final Map<String, Long> lastAlertTime = new HashMap<>();

    public PlayerData(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.lastLocation = player.getLocation();
        this.previousLocation = player.getLocation();
    }

    // ── Violations ────────────────────────────────────────────────
    public int getViolations(String check) {
        return violations.getOrDefault(check.toLowerCase(), 0);
    }
    public int addViolation(String check) {
        int v = violations.getOrDefault(check.toLowerCase(), 0) + 1;
        violations.put(check.toLowerCase(), v);
        return v;
    }
    public void clearViolations(String check) { violations.remove(check.toLowerCase()); }
    public void clearAllViolations()          { violations.clear(); }
    public Map<String, Integer> getAllViolations() { return Collections.unmodifiableMap(violations); }
    public int getTotalViolations() { return violations.values().stream().mapToInt(Integer::intValue).sum(); }

    // ── Alert cooldown ────────────────────────────────────────────
    public boolean canAlert(String check, long cooldownMs) {
        long now = System.currentTimeMillis();
        long last = lastAlertTime.getOrDefault(check, 0L);
        if (now - last >= cooldownMs) {
            lastAlertTime.put(check, now);
            return true;
        }
        return false;
    }

    // ── Getters / Setters ─────────────────────────────────────────
    public UUID getUuid()    { return uuid; }
    public String getName()  { return name; }

    public long getLastPacketTime()             { return lastPacketTime; }
    public void setLastPacketTime(long t)       { lastPacketTime = t; }
    public int  getPacketCount()                { return packetCount; }
    public void setPacketCount(int c)           { packetCount = c; }
    public long getPacketSecond()               { return packetSecond; }
    public void setPacketSecond(long t)         { packetSecond = t; }

    public Location getLastLocation()           { return lastLocation; }
    public void setLastLocation(Location l)     { previousLocation = lastLocation; lastLocation = l; }
    public Location getPreviousLocation()       { return previousLocation; }
    public double getLastDeltaY()               { return lastDeltaY; }
    public void setLastDeltaY(double d)         { lastDeltaY = d; }
    public boolean wasOnGround()                { return wasOnGround; }
    public boolean isOnGround()                 { return isOnGround; }
    public void setOnGround(boolean g)          { wasOnGround = isOnGround; isOnGround = g; }
    public int  getAirTicks()                   { return airTicks; }
    public void setAirTicks(int t)              { airTicks = t; }
    public long getLastMoveTime()               { return lastMoveTime; }
    public void setLastMoveTime(long t)         { lastMoveTime = t; }
    public Deque<Location> getLocationHistory() { return locationHistory; }

    public long getLastAttackTime()             { return lastAttackTime; }
    public void setLastAttackTime(long t)       { lastAttackTime = t; }
    public int  getClicksThisSecond()           { return clicksThisSecond; }
    public void setClicksThisSecond(int c)      { clicksThisSecond = c; }
    public long getCpsSecond()                  { return cpsSecond; }
    public void setCpsSecond(long t)            { cpsSecond = t; }
    public long getLastSwingTime()              { return lastSwingTime; }
    public void setLastSwingTime(long t)        { lastSwingTime = t; }
    public boolean isSwungThisTick()            { return swungThisTick; }
    public void setSwungThisTick(boolean b)     { swungThisTick = b; }
    public long getLastDamageTime()             { return lastDamageTime; }
    public void setLastDamageTime(long t)       { lastDamageTime = t; }

    public long getLastArmSwingPacket()         { return lastArmSwingPacket; }
    public void setLastArmSwingPacket(long t)   { lastArmSwingPacket = t; }

    public Location getLastInteractLocation()           { return lastInteractLocation; }
    public void setLastInteractLocation(Location l)     { lastInteractLocation = l; }

    public long getLastCrystalPlaceTime()               { return lastCrystalPlaceTime; }
    public void setLastCrystalPlaceTime(long t)         { lastCrystalPlaceTime = t; }
    public int  getCrystalPlacesPerSecond()             { return crystalPlacesPerSecond; }
    public void setCrystalPlacesPerSecond(int c)        { crystalPlacesPerSecond = c; }
    public long getCrystalSecond()                      { return crystalSecond; }
    public void setCrystalSecond(long t)                { crystalSecond = t; }
    public long getLastCrystalExplodeTime()             { return lastCrystalExplodeTime; }
    public void setLastCrystalExplodeTime(long t)       { lastCrystalExplodeTime = t; }

    public long getLastTotemUseTime()                   { return lastTotemUseTime; }
    public void setLastTotemUseTime(long t)             { lastTotemUseTime = t; }
    public long getLastTotemEquipTime()                 { return lastTotemEquipTime; }
    public void setLastTotemEquipTime(long t)           { lastTotemEquipTime = t; }

    public long getLastPotionThrowTime()                { return lastPotionThrowTime; }
    public void setLastPotionThrowTime(long t)          { lastPotionThrowTime = t; }

    public long getBowDrawStart()                       { return bowDrawStart; }
    public void setBowDrawStart(long t)                 { bowDrawStart = t; }

    public boolean isInventoryOpen()                    { return inventoryOpen; }
    public void setInventoryOpen(boolean b)             { inventoryOpen = b; }

    public long getLastBlockPlaceTime()                 { return lastBlockPlaceTime; }
    public void setLastBlockPlaceTime(long t)           { lastBlockPlaceTime = t; }
    public int  getScaffoldCount()                      { return scaffoldCount; }
    public void setScaffoldCount(int c)                 { scaffoldCount = c; }
}
