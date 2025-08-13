

/**
 * AutoFish Module
 */
package net.payload.module.modules.misc;

import net.payload.Payload;
import net.payload.event.events.ReceivePacketEvent;
import net.payload.event.listeners.ReceivePacketListener;
import net.payload.module.Category;
import net.payload.module.Module;
import net.payload.settings.types.BooleanSetting;
import net.payload.utils.FindItemResult;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class AutoFish extends Module implements ReceivePacketListener {
	private BooleanSetting autoSwitch = BooleanSetting.builder().id("autofish_autoswitch").displayName("Auto Switch")
			.description("Automatically switch to fishing rod before casting.").defaultValue(true).build();

	private BooleanSetting autoToggle = BooleanSetting.builder().id("autofish_autotoggle").displayName("Auto Toggle")
			.description("Automatically toggles off if no fishing rod is found in the hotbar.").defaultValue(false)
			.build();

	public AutoFish() {
		super("AutoFish");

		this.setCategory(Category.of("Misc"));
		this.setDescription("Automatically goes fishing for you");

		this.addSetting(autoSwitch);
		this.addSetting(autoToggle);
	}

	@Override
	public void onDisable() {
		Payload.getInstance().eventManager.RemoveListener(ReceivePacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Payload.getInstance().eventManager.AddListener(ReceivePacketListener.class, this);

		FindItemResult rod = find(Items.FISHING_ROD);

		if (autoSwitch.getValue()) {
			if (rod.found() && rod.isHotbar()) {
				swap(rod.slot(), false);
			} else {
				if (!autoToggle.getValue())
					return;

				toggle();
			}
		}
	}

	@Override
	public void onToggle() {

	}

	private void castRod(int count) {
		FindItemResult rod = find(Items.FISHING_ROD);

		if (autoSwitch.getValue()) {
			if (rod.found() && rod.isHotbar()) {
				swap(rod.slot(), false);
			} else {
				if (!autoToggle.getValue())
					return;

				toggle();
			}
		}

		for (int i = 0; i < count; i++) {
			MC.interactionManager.interactItem(MC.player, Hand.MAIN_HAND);
		}
	}

	@Override
	public void onReceivePacket(ReceivePacketEvent readPacketEvent) {
		Packet<?> packet = readPacketEvent.GetPacket();

		if (packet instanceof PlaySoundS2CPacket) {
			PlaySoundS2CPacket soundPacket = (PlaySoundS2CPacket) packet;
			if (soundPacket.getSound().value().equals(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH)) {
				castRod(2);
			}
		}
	}
}
