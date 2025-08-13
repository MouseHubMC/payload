package net.payload.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.payload.Payload;
import net.payload.event.events.TickEvent;
import net.payload.event.listeners.TickListener;
import net.payload.module.Category;
import net.payload.module.Module;
import net.payload.settings.types.BooleanSetting;
import net.payload.settings.types.FloatSetting;

import java.util.Random;

public class TriggerBot extends Module implements TickListener {

    private final BooleanSetting targetPlayers = BooleanSetting.builder()
            .id("triggerbot_players")
            .displayName("Players")
            .defaultValue(true)
            .build();

    private final BooleanSetting targetMobs = BooleanSetting.builder()
            .id("triggerbot_mobs")
            .displayName("Mobs")
            .defaultValue(false)
            .build();

    private final BooleanSetting targetEndCrystals = BooleanSetting.builder()
            .id("triggerbot_endcrystals")
            .displayName("End Crystals")
            .defaultValue(false)
            .build();

    private final BooleanSetting targetArmorStands = BooleanSetting.builder()
            .id("triggerbot_armorstands")
            .displayName("Armor Stands")
            .defaultValue(false)
            .build();

    private final FloatSetting range = FloatSetting.builder()
            .id("triggerbot_range")
            .displayName("Range")
            .defaultValue(4.0f)
            .minValue(0.0f)
            .maxValue(6.0f)
            .step(0.1f)
            .build();

    private final FloatSetting chance = FloatSetting.builder()
            .id("triggerbot_chance")
            .displayName("Chance (%)")
            .defaultValue(100.0f)
            .minValue(0.0f)
            .maxValue(100.0f)
            .step(1.0f)
            .build();

    private final FloatSetting cooldownPercent = FloatSetting.builder()
            .id("triggerbot_cooldown_percent")
            .displayName("Attack Cooldown %")
            .defaultValue(100.0f)
            .minValue(0.0f)
            .maxValue(100.0f)
            .step(1.0f)
            .build();

    private final FloatSetting reactionDelayMin = FloatSetting.builder()
            .id("triggerbot_reaction_min")
            .displayName("Min Reaction Delay (ms)")
            .defaultValue(50.0f)
            .minValue(0.0f)
            .maxValue(1000.0f)
            .step(1.0f)
            .build();

    private final FloatSetting reactionDelayMax = FloatSetting.builder()
            .id("triggerbot_reaction_max")
            .displayName("Max Reaction Delay (ms)")
            .defaultValue(150.0f)
            .minValue(0.0f)
            .maxValue(1000.0f)
            .step(1.0f)
            .build();

    private final BooleanSetting requireWeapon = BooleanSetting.builder()
            .id("triggerbot_require_weapon")
            .displayName("Require Weapon")
            .defaultValue(true)
            .build();

    private long lastAttackTime = 0;
    private final Random random = new Random();

    public TriggerBot() {
        super("TriggerBot");
        this.setCategory(Category.of("Combat"));
        this.setDescription("Automatically attacks entities with configurable targets, cooldown, and reaction delay.");

        this.addSetting(targetPlayers);
        this.addSetting(targetMobs);
        this.addSetting(targetEndCrystals);
        this.addSetting(targetArmorStands);
        this.addSetting(range);
        this.addSetting(chance);
        this.addSetting(cooldownPercent);
        this.addSetting(reactionDelayMin);
        this.addSetting(reactionDelayMax);
        this.addSetting(requireWeapon);
    }

    @Override
    public void onEnable() {
        Payload.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    /**
     * 
     */
    @Override
    public void onToggle() {

    }

    @Override
    public void onDisable() {
        Payload.getInstance().eventManager.RemoveListener(TickListener.class, this);
    }

    /**
     * @param event 
     */
    @Override
    public void onTick(TickEvent.Pre event) {
        
    }

    @Override
    public void onTick(TickEvent.Post event) {
        if (MC.player == null || MC.world == null || MC.crosshairTarget == null) return;

        if (MC.crosshairTarget.getType() != HitResult.Type.ENTITY) return;

        EntityHitResult hit = (EntityHitResult) MC.crosshairTarget;
        Entity target = hit.getEntity();
        if (target == null || !isValidTarget(target)) return;

        if (requireWeapon.getValue() && !isWeapon(MC.player.getMainHandStack().getItem())) return;
        if (MC.player.distanceTo(target) > range.getValue()) return;

        // Check attack cooldown
        float attackStrength = MC.player.getAttackCooldownProgress(0f) * 100f;
        if (attackStrength < cooldownPercent.getValue()) return;

        // Reaction delay
        long now = System.currentTimeMillis();
        float minDelay = reactionDelayMin.getValue();
        float maxDelay = reactionDelayMax.getValue();
        int delay = random.nextInt((int)(maxDelay - minDelay + 1)) + (int)minDelay;


        if (now - lastAttackTime < delay) return;

        // Chance check
        if (random.nextFloat() * 100f > chance.getValue()) return;

        // Attack
        MC.interactionManager.attackEntity(MC.player, target);
        MC.player.swingHand(Hand.MAIN_HAND);

        lastAttackTime = now;
    }

    private boolean isWeapon(Item item) {
        return item instanceof SwordItem || item instanceof AxeItem || item instanceof BowItem ||
                item instanceof CrossbowItem || item instanceof TridentItem;
    }

    private boolean isValidTarget(Entity e) {
        if (e instanceof PlayerEntity && targetPlayers.getValue()) return e != MC.player;
        if (e instanceof MobEntity && targetMobs.getValue()) return true;
        if (e.getType().toString().toLowerCase().contains("end_crystal") && targetEndCrystals.getValue()) return true;
        if (e instanceof ArmorStandEntity && targetArmorStands.getValue()) return true;
        return false;
    }
}
