package net.payload.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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

public class AutoClicker extends Module implements TickListener {


    private final FloatSetting chance = FloatSetting.builder().id("autoclicker_chance").displayName("Chance (%)").description("Chance to perform a click each cycle.").defaultValue(100.0f).minValue(0.0f).maxValue(100.0f).step(1.0f).build();

    private final FloatSetting cpsMin = FloatSetting.builder().id("autoclicker_cps_min").displayName("CPS Min").description("Minimum clicks per second.").defaultValue(8.0f).minValue(0.1f).maxValue(20.0f).step(0.1f).build();

    private final FloatSetting cpsMax = FloatSetting.builder().id("autoclicker_cps_max").displayName("CPS Max").description("Maximum clicks per second.").defaultValue(12.0f).minValue(0.1f).maxValue(20.0f).step(0.1f).build();

    private final BooleanSetting requireWeapon = BooleanSetting.builder().id("autoclicker_require_weapon").displayName("Require Weapon").description("Only clicks when holding a weapon.").defaultValue(true).build();

    private final BooleanSetting preventBlockBreak = BooleanSetting.builder().id("autoclicker_no_block").displayName("Prevent Block Break").description("Won't break blocks, only attacks entities.").defaultValue(true).build();

    private final BooleanSetting targetPlayers = BooleanSetting.builder().id("autoclicker_players").displayName("Players").defaultValue(true).build();

    private final BooleanSetting targetMobs = BooleanSetting.builder().id("autoclicker_mobs").displayName("Mobs").defaultValue(false).build();

    private final BooleanSetting targetEndCrystals = BooleanSetting.builder().id("autoclicker_endcrystals").displayName("End Crystals").defaultValue(false).build();

    private final BooleanSetting targetArmorStands = BooleanSetting.builder().id("autoclicker_armorstands").displayName("Armor Stands").defaultValue(false).build();

    private final FloatSetting attackCooldown = FloatSetting.builder().id("autoclicker_cooldown").displayName("Attack Cooldown (%)").description("Only attack when attack cooldown is at or below this %.").defaultValue(100.0f).minValue(0.0f).maxValue(100.0f).step(1.0f).build();

    private final FloatSetting reactionDelayMin = FloatSetting.builder().id("autoclicker_reaction_min").displayName("Reaction Delay Min (ms)").description("Minimum reaction delay before attacking.").defaultValue(0.0f).minValue(0.0f).maxValue(1000.0f).step(1.0f).build();

    private final FloatSetting reactionDelayMax = FloatSetting.builder().id("autoclicker_reaction_max").displayName("Reaction Delay Max (ms)").description("Maximum reaction delay before attacking.").defaultValue(200.0f).minValue(0.0f).maxValue(2000.0f).step(1.0f).build();

    private long lastClickTime = 0;
    private long nextClickDelay = 0;

    public AutoClicker() {
        super("AutoClicker");
        this.setCategory(Category.of("Combat"));
        this.setDescription("Automatically attacks entities with configurable CPS and targeting.");

        this.addSetting(chance);
        this.addSetting(cpsMin);
        this.addSetting(cpsMax);
        this.addSetting(requireWeapon);
        this.addSetting(preventBlockBreak);

        this.addSetting(targetPlayers);
        this.addSetting(targetMobs);
        this.addSetting(targetEndCrystals);
        this.addSetting(targetArmorStands);

        this.addSetting(attackCooldown);
        this.addSetting(reactionDelayMin);
        this.addSetting(reactionDelayMax);
    }

    @Override
    public void onEnable() {
        Payload.getInstance().eventManager.AddListener(TickListener.class, this);
        lastClickTime = 0;
        nextClickDelay = 0;
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

    private boolean isWeapon(Item item) {
        return item instanceof SwordItem || item instanceof AxeItem || item instanceof BowItem || item instanceof CrossbowItem || item instanceof TridentItem;
    }

    private boolean isValidTarget(Entity e) {
        if (e instanceof PlayerEntity && targetPlayers.getValue()) return true;
        if (e instanceof MobEntity && targetMobs.getValue()) return true;
        if (e.getType().toString().toLowerCase().contains("end_crystal") && targetEndCrystals.getValue()) return true;
        return e.getType() == EntityType.ARMOR_STAND && targetArmorStands.getValue();
    }


    @Override
    public void onTick(TickEvent.Pre event) {
        if (MC.player == null || MC.world == null) return;
        if (requireWeapon.getValue() && !isWeapon(MC.player.getMainHandStack().getItem())) return;
        if (Math.random() * 100 > chance.getValue()) return;

        // If preventBlockBreak is true and crosshair isn't on an entity, don't click
        if (preventBlockBreak.getValue() && (MC.crosshairTarget == null || MC.crosshairTarget.getType() != HitResult.Type.ENTITY))
            return;

        Entity target = null;
        if (MC.crosshairTarget != null && MC.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            target = ((EntityHitResult) MC.crosshairTarget).getEntity();
        }

        // Only attack if we have a valid target
        if (target == null || !isValidTarget(target)) return;

        // Check attack cooldown %
        float cooldownPercent = MC.player.getAttackCooldownProgress(0.0f) * 100.0f;
        if (cooldownPercent < attackCooldown.getValue()) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < nextClickDelay) return;

        // Swing and attack
        MC.player.swingHand(Hand.MAIN_HAND);
        assert MC.interactionManager != null;
        MC.interactionManager.attackEntity(MC.player, target);

        lastClickTime = currentTime;
        double cps = cpsMin.getValue() + Math.random() * (cpsMax.getValue() - cpsMin.getValue());
        if (cps <= 0) cps = 1;

        double delay = reactionDelayMin.getValue() + Math.random() * (reactionDelayMax.getValue() - reactionDelayMin.getValue());
        nextClickDelay = Math.max((long) (1000 / cps), (long) delay);
    }

    /**
     * @param event blah blah
     */
    @Override
    public void onTick(TickEvent.Post event) {

    }

}