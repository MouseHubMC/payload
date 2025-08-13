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
import net.payload.settings.types.EnumSetting;
import net.payload.settings.types.FloatSetting;

public class AutoClicker extends Module implements TickListener {

    public enum TargetType {
        Players, Mobs, EndCrystals, Misc
    }

    private final FloatSetting chance = FloatSetting.builder().id("autoclicker_chance").displayName("Chance (%)").description("Chance to perform a click each cycle.").defaultValue(100.0f).minValue(0.0f).maxValue(100.0f).step(1.0f).build();

    private final FloatSetting cpsMin = FloatSetting.builder().id("autoclicker_cps_min").displayName("CPS Min").description("Minimum clicks per second.").defaultValue(8.0f).minValue(0.1f).maxValue(20.0f).step(0.1f).build();

    private final FloatSetting cpsMax = FloatSetting.builder().id("autoclicker_cps_max").displayName("CPS Max").description("Maximum clicks per second.").defaultValue(12.0f).minValue(0.1f).maxValue(20.0f).step(0.1f).build();

    private final BooleanSetting requireWeapon = BooleanSetting.builder().id("autoclicker_require_weapon").displayName("Require Weapon").description("Only clicks when holding a weapon.").defaultValue(true).build();

    private final BooleanSetting preventBlockBreak = BooleanSetting.builder().id("autoclicker_no_block").displayName("Prevent Block Break").description("Won't break blocks, only attacks entities.").defaultValue(true).build();

    private final EnumSetting<TargetType> targetType = EnumSetting.<TargetType>builder().id("autoclicker_target_type").displayName("Target Type").description("Which type of entities to attack.").defaultValue(TargetType.Players).build();

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
        this.addSetting(targetType);
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
        switch (targetType.getValue()) {
            case Players:
                return e instanceof PlayerEntity && e != MC.player;
            case Mobs:
                return e instanceof MobEntity;
            case EndCrystals:
                return e.getType().toString().toLowerCase().contains("end_crystal");
            case Misc:
                EntityType<?> type = e.getType();
                return type == EntityType.ARMOR_STAND
                        // Boat
                        || type == EntityType.OAK_BOAT || type == EntityType.SPRUCE_BOAT || type == EntityType.BIRCH_BOAT || type == EntityType.JUNGLE_BOAT || type == EntityType.ACACIA_BOAT || type == EntityType.DARK_OAK_BOAT || type == EntityType.MANGROVE_BOAT || type == EntityType.CHERRY_BOAT || type == EntityType.BAMBOO_RAFT
                        // Chest Boats
                        || type == EntityType.OAK_CHEST_BOAT || type == EntityType.SPRUCE_CHEST_BOAT || type == EntityType.BIRCH_CHEST_BOAT || type == EntityType.JUNGLE_CHEST_BOAT || type == EntityType.ACACIA_CHEST_BOAT || type == EntityType.DARK_OAK_CHEST_BOAT || type == EntityType.MANGROVE_CHEST_BOAT || type == EntityType.CHERRY_CHEST_BOAT || type == EntityType.BAMBOO_CHEST_RAFT
                        // Minecarts
                        || type == EntityType.MINECART || type == EntityType.CHEST_MINECART || type == EntityType.FURNACE_MINECART || type == EntityType.TNT_MINECART || type == EntityType.HOPPER_MINECART || type == EntityType.SPAWNER_MINECART
                        // Other/misc
                        || type == EntityType.ITEM_FRAME || type == EntityType.GLOW_ITEM_FRAME || type == EntityType.PAINTING || type == EntityType.EXPERIENCE_ORB || type == EntityType.FALLING_BLOCK || type == EntityType.ITEM;

            default:
                return false;
        }
    }

    @Override
    public void onTick(TickEvent.Pre event) {
        if (MC.player == null || MC.world == null) return;
        if (requireWeapon.getValue() && !isWeapon(MC.player.getMainHandStack().getItem())) return;
        if (Math.random() * 100 > chance.getValue()) return;

        // Check if crosshair is on an entity
        if (MC.crosshairTarget == null || MC.crosshairTarget.getType() != HitResult.Type.ENTITY) return;

        Entity target = ((EntityHitResult) MC.crosshairTarget).getEntity();
        if (target == null || !isValidTarget(target)) return;

        // Check attack cooldown %
        float cooldownPercent = MC.player.getAttackCooldownProgress(0.0f) * 100.0f;
        if (cooldownPercent < attackCooldown.getValue()) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < nextClickDelay) return;

        // Swing and attack
        MC.player.swingHand(Hand.MAIN_HAND);
        MC.interactionManager.attackEntity(MC.player, target);

        lastClickTime = currentTime;
        double cps = cpsMin.getValue() + Math.random() * (cpsMax.getValue() - cpsMin.getValue());
        if (cps <= 0) cps = 1;

        // Reaction delay between clicks
        double delay = reactionDelayMin.getValue() + Math.random() * (reactionDelayMax.getValue() - reactionDelayMin.getValue());
        nextClickDelay = Math.max((long) (1000 / cps), (long) delay);
    }

    /**
     * @param event
     */
    @Override
    public void onTick(TickEvent.Post event) {

    }

}