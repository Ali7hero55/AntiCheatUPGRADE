package dev.rjac.manager;

import dev.rjac.RJ_AC;
import dev.rjac.checks.combat.*;
import dev.rjac.checks.crystal.*;
import dev.rjac.checks.movement.*;
import dev.rjac.checks.misc.*;

public class CheckManager {

    private final RJ_AC plugin;

    // Combat
    public final KillAuraCheck killAura;
    public final ReachCheck reach;
    public final VelocityCheck velocity;
    public final AutoClickerCheck autoClicker;
    public final AimBotCheck aimBot;
    public final CritSpamCheck critSpam;
    public final NoSwingCheck noSwing;
    public final BacktrackCheck backtrack;
    public final MultiAuraCheck multiAura;
    public final HitBoxCheck hitBox;

    // Crystal
    public final CrystalAuraCheck crystalAura;
    public final AutoCrystalCheck autoCrystal;
    public final FastCrystalPlaceCheck fastCrystalPlace;
    public final SelfCrystalCheck selfCrystal;
    public final AutoTotemCheck autoTotem;

    // Movement
    public final SpeedCheck speed;
    public final FlightCheck flight;
    public final NoFallCheck noFall;
    public final BlinkCheck blink;
    public final PhaseCheck phase;
    public final StepCheck step;
    public final JesusCheck jesus;
    public final TimerCheck timer;
    public final FastLadderCheck fastLadder;

    // Misc
    public final ScaffoldCheck scaffold;
    public final FastBowCheck fastBow;
    public final BadPacketsCheck badPackets;
    public final InventoryMoveCheck inventoryMove;
    public final AutoPotCheck autoPot;

    public CheckManager(RJ_AC plugin) {
        this.plugin = plugin;
        killAura       = new KillAuraCheck(plugin);
        reach          = new ReachCheck(plugin);
        velocity       = new VelocityCheck(plugin);
        autoClicker    = new AutoClickerCheck(plugin);
        aimBot         = new AimBotCheck(plugin);
        critSpam       = new CritSpamCheck(plugin);
        noSwing        = new NoSwingCheck(plugin);
        backtrack      = new BacktrackCheck(plugin);
        multiAura      = new MultiAuraCheck(plugin);
        hitBox         = new HitBoxCheck(plugin);
        crystalAura    = new CrystalAuraCheck(plugin);
        autoCrystal    = new AutoCrystalCheck(plugin);
        fastCrystalPlace = new FastCrystalPlaceCheck(plugin);
        selfCrystal    = new SelfCrystalCheck(plugin);
        autoTotem      = new AutoTotemCheck(plugin);
        speed          = new SpeedCheck(plugin);
        flight         = new FlightCheck(plugin);
        noFall         = new NoFallCheck(plugin);
        blink          = new BlinkCheck(plugin);
        phase          = new PhaseCheck(plugin);
        step           = new StepCheck(plugin);
        jesus          = new JesusCheck(plugin);
        timer          = new TimerCheck(plugin);
        fastLadder     = new FastLadderCheck(plugin);
        scaffold       = new ScaffoldCheck(plugin);
        fastBow        = new FastBowCheck(plugin);
        badPackets     = new BadPacketsCheck(plugin);
        inventoryMove  = new InventoryMoveCheck(plugin);
        autoPot        = new AutoPotCheck(plugin);
    }
}
