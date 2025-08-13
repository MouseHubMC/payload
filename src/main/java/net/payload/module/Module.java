

/**
 * A class to represent a generic module.
 */
package net.payload.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.payload.Payload;
import net.payload.PayloadClient;
import net.payload.SoundGenerator;
import net.payload.interfaces.IClientPlayerInteractionManager;
import org.lwjgl.glfw.GLFW;

import net.payload.mixin.interfaces.IMinecraftClient;
import net.payload.settings.Setting;
import net.payload.settings.SettingManager;
import net.payload.settings.types.BooleanSetting;
import net.payload.settings.types.KeybindSetting;
import net.payload.utils.FindItemResult;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class Module {
	private String name;
	private String description;
	private Category category;

	public final BooleanSetting state;
	public final KeybindSetting keyBind;
	private List<Setting<?>> settings = new ArrayList<Setting<?>>();

	protected static final MinecraftClient MC = PayloadClient.MC;
	protected final IMinecraftClient IMC = PayloadClient.IMC;


	public Module(String name) {
		this(name, InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0));
	}

	public Module(String name, Key keybind) {
		this.name = name;
		this.keyBind = KeybindSetting.builder().id("key." + name.toLowerCase()).displayName(name + " Key")
				.defaultValue(keybind).build();

		this.state = BooleanSetting.builder().id("state." + name.toLowerCase()).displayName(name + " State")
				.defaultValue(false).onUpdate(s -> {
					if (s) {
						this.onEnable();
						Payload.getInstance().moduleManager.chatModule.moduleToggle(name + Formatting.GREEN + " [ON]");
						SoundGenerator.turnOn();
					}
					else {
						this.onDisable();
						Payload.getInstance().moduleManager.chatModule.moduleToggle(name + Formatting.RED + " [OFF]");
						SoundGenerator.turnOff();
					}
					this.onToggle();
				}).build();

		this.addSetting(keyBind);
		this.addSetting(state);

		SettingManager.registerSetting(this.keyBind);
		SettingManager.registerSetting(this.state);
	}

	/**
	 * Retrieves the name of the module.
	 *
	 * @return The name of the module.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the module.
	 *
	 * @param name The name to assign to the module.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves the description of the module.
	 *
	 * @return The description of the module.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description of the module.
	 *
	 * @param description The description to assign to the module.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Retrieves the category of the module.
	 *
	 * @return The {@link Category} assigned to this module.
	 */
	public Category getCategory() {
		return this.category;
	}

	/**
	 * Sets the category for the module.
	 *
	 * @param category The {@link Category} to assign to this module, categorizing
	 *                 it for organizational purposes.
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	/**
	 * Retrieves the keybind associated with the module.
	 *
	 * @return The {@link KeybindSetting} that represents the keybinding for this
	 *         module.
	 */
	public KeybindSetting getBind() {
		return this.keyBind;
	}

	/**
	 * Adds a setting to the module.
	 *
	 * @param setting The {@link Setting} to be added to the module's settings list.
	 */
	public void addSetting(Setting<?> setting) {
		this.settings.add(setting);
	}

	/**
	 * Retrieves the list of settings associated with the module.
	 *
	 * @return A {@link List} of {@link Setting} objects that belong to this module.
	 */
	public List<Setting<?>> getSettings() {
		return this.settings;
	}

	/**
	 * Checks if the module has any settings defined.
	 *
	 * @return {@code true} if the module has one or more settings; {@code false}
	 *         otherwise.
	 */
	public boolean hasSettings() {
		return !this.settings.isEmpty();
	}

	/**
	 * Called when the module is disabled.
	 * <p>
	 * This method should contain the logic that needs to be executed when the
	 * module is turned off, such as stopping certain processes or resetting states.
	 * </p>
	 */
	public abstract void onDisable();

	/**
	 * Called when the module is enabled.
	 * <p>
	 * This method should contain the logic that needs to be executed when the
	 * module is activated, such as starting certain processes or modifying game
	 * states.
	 * </p>
	 */
	public abstract void onEnable();

	/**
	 * Called when the module's state is toggled.
	 * <p>
	 * This method serves as a general handler that is invoked whenever the module
	 * is enabled or disabled. It can contain logic that needs to be executed
	 * regardless of the current state.
	 * </p>
	 */
	public abstract void onToggle();

	/**
	 * Checks if a specific key is currently pressed.
	 *
	 * @param button The key code of the key to check. A value of -1 indicates an
	 *               invalid key.
	 * @return {@code true} if the specified key is pressed; {@code false}
	 *         otherwise. If the button is less than 10 or equals -1, it will return
	 *         {@code false}.
	 */
	public boolean isKeyPressed(int button) {
		if (button == -1)
			return false;

		if (button < 10) // check
			return false;

		return InputUtil.isKeyPressed(MC.getWindow().getHandle(), button);
	}

	/**
	 * Toggles the state of the module between enabled and disabled.
	 * <p>
	 * If the module is currently enabled, it will call the {@link #onDisable()}
	 * method to handle any necessary actions during disabling. Conversely, if the
	 * module is disabled, it will call the {@link #onEnable()} method to perform
	 * actions upon enabling.
	 * </p>
	 * The state is then updated accordingly.
	 */
	public void toggle() {
		state.setValue(!state.getValue());
	}

	/**
	 * Returns the current status of the module as a string.
	 */
	public String getStatus() {
		return this.state.getValue() ? "Enabled" : "Disabled";
	}

	/**
	 * Retrieves the current keybind as a string.
	 */
	public String getKeyBindDisplayName() {
		return keyBind.displayName;
	}

	/**
	 * Resets the module settings to their default values.
	 */
	public void resetSettings() {
		for (Setting<?> setting : settings) {
			setting.resetToDefault();
		}
	}

	/**
	 * Checks if the module belongs to a specified category.
	 *
	 * @param category The {@link Category} to check against this module's category.
	 * @return {@code true} if the module is in the specified category;
	 *         {@code false} otherwise.
	 */
	public final boolean isCategory(Category category) {
		return category.equals(this.category);
	}

	/**
	 * Annotation to provide metadata about a module.
	 * <p>
	 * This annotation can be used to specify module information such as name,
	 * description, category, and keybind.
	 * </p>
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ModInfo {
		/**
		 * @return The name of the module.
		 */
		String name();

		/**
		 * @return A brief description of the module's functionality.
		 */
		String description();

		/**
		 * @return The category which the module belongs to.
		 */
		String category();

		/**
		 * @return The keybind integer associated with the module.
		 */
		int bind();
	}

	public static int previousSlot = -1;

	public static FindItemResult findInHotbar(Item... items) {
		return findInHotbar(itemStack -> {
			for (Item item : items) {
				if (itemStack.getItem() == item)
					return true;
			}
			return false;
		});
	}

	public static FindItemResult findInHotbar(Predicate<ItemStack> isGood) {
		if (testInOffHand(isGood)) {
			return new FindItemResult(45, MC.player.getOffHandStack().getCount());
		}

		if (testInMainHand(isGood)) {
			return new FindItemResult(MC.player.getInventory().selectedSlot, MC.player.getMainHandStack().getCount());
		}

		return find(isGood, 0, 8);
	}

	public static FindItemResult find(Predicate<ItemStack> isGood) {
		if (MC.player == null) {
			return new FindItemResult(0, 0);
		}

		return find(isGood, 0, MC.player.getInventory().size());
	}

	public static FindItemResult find(Predicate<ItemStack> isGood, int start, int end) {
		if (MC.player == null) {
			return new FindItemResult(0, 0);
		}

		int slot = -1;
		int count = 0;

		for (int i = start; i <= end; i++) {
			ItemStack stack = MC.player.getInventory().getStack(i);

			if (isGood.test(stack)) {
				if (slot == -1) {
					slot = i;
				}
				count += stack.getCount();
			}
		}

		return new FindItemResult(slot, count);
	}

	public static FindItemResult findFastestTool(BlockState state) {
		float bestScore = 1;
		int slot = -1;

		for (int i = 0; i < 9; i++) {
			ItemStack stack = MC.player.getInventory().getStack(i);

			if (stack.isSuitableFor(state)) {
				float score = stack.getMiningSpeedMultiplier(state);

				if (score > bestScore) {
					bestScore = score;
					slot = i;
				}
			}
		}

		return new FindItemResult(slot, 1);
	}

	public static boolean testInMainHand(Predicate<ItemStack> predicate) {
		return predicate.test(MC.player.getMainHandStack());
	}

	public static boolean testInOffHand(Predicate<ItemStack> predicate) {
		return predicate.test(MC.player.getOffHandStack());
	}

	public static boolean swap(int slot, boolean swapBack) {
		if (slot == 45) {
			return true;
		}

		if (slot < 0 || slot > 8) {
			return false;
		}

		if (swapBack) {
			if (previousSlot == -1) {
				previousSlot = MC.player.getInventory().selectedSlot;
			}
		} else {
			previousSlot = -1;
		}

		MC.player.getInventory().selectedSlot = slot;
		((IClientPlayerInteractionManager) MC.interactionManager).payload$syncSelected();
		return true;
	}

	public static boolean swapBack() {
		if (previousSlot == -1) {
			return false;
		}

		boolean result = swap(previousSlot, false);
		previousSlot = -1;
		return result;
	}

	public static FindItemResult find(Item... items) {
		return find(itemStack -> {
			for (Item item : items) {
				if (itemStack.getItem() == item)
					return true;
			}
			return false;
		});
	}

	public void keepEnabled() {
		if (!this.state.getValue()) {
			this.toggle();
		}
	}

	public static void sendSequencedPacket(SequencedPacketCreator packetCreator) {
		if (MC.getNetworkHandler() == null || MC.world == null) return;
		try (PendingUpdateManager pendingUpdateManager = MC.world.getPendingUpdateManager().incrementSequence()) {
			int i = pendingUpdateManager.getSequence();
			MC.getNetworkHandler().sendPacket(packetCreator.predict(i));
		}
	}

	public static boolean nullCheck() {
		return MC.player == null || MC.world == null;
	}

	public static void sendChatMessage(String message) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.inGameHud != null) {
			mc.inGameHud.getChatHud().addMessage(Text.of(Formatting.DARK_PURPLE + "[" + Formatting.LIGHT_PURPLE + "Payload"
					+ Formatting.DARK_PURPLE + "] " + Formatting.RESET + message));
		}
	}

	public static void sendTotemMessage(String message) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.inGameHud != null) {
			mc.inGameHud.getChatHud().addMessage(Text.of(Formatting.DARK_GREEN + "[" + Formatting.GREEN + "AutoTotem"
					+ Formatting.DARK_GREEN + "] " + Formatting.RESET + message));
		}
	}

	public static void sendErrorMessage(String message) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.inGameHud != null) {
			mc.inGameHud.getChatHud().addMessage(Text.of(Formatting.RED + "[" + Formatting.DARK_RED + "Payload Error"
					+ Formatting.RED + "] " + Formatting.RESET + message));
		}
	}
}