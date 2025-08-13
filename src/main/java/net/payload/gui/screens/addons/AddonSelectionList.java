

package net.payload.gui.screens.addons;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.payload.PayloadClient;
import org.jetbrains.annotations.Nullable;

import net.payload.api.IAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AddonSelectionList extends AlwaysSelectedEntryListWidget<AddonSelectionList.Entry> {
	private final List<NormalEntry> addonList = new ArrayList<AddonSelectionList.NormalEntry>();

	private AddonScreen parent;

	public AddonSelectionList(AddonScreen ownerIn, MinecraftClient minecraftClient, int i, int j, int k, int l) {
		super(minecraftClient, i, j, k, l);
		parent = ownerIn;
		updateAddonList();
	}

	public void updateAddonList() {
		this.clearEntries();
		for (IAddon addon : PayloadClient.addons) {
			AddonSelectionList.NormalEntry entry = new AddonSelectionList.NormalEntry(this, addon);
			addonList.add(entry);
		}
		this.addonList.forEach(this::addEntry);
	}

	public List<NormalEntry> getAddons() {
		return this.addonList;
	}

	public void setSelected(@Nullable NormalEntry entry) {
		super.setSelected(entry);
	}

	public void onClickEntry(@Nullable NormalEntry entry) {
		parent.setSelected(entry);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		Entry AltSelectionList$entry = this.getSelectedOrNull();
		return AltSelectionList$entry != null && AltSelectionList$entry.keyPressed(keyCode, scanCode, modifiers)
				|| super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Environment(value = EnvType.CLIENT)
	public static abstract class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> implements AutoCloseable {
		@Override
		public void close() {
		}
	}

	public class NormalEntry extends Entry {
		private final AddonSelectionList owner;
		private final MinecraftClient mc;
		private final Identifier iconIdentifier;
		private final IAddon addon;

		protected NormalEntry(AddonSelectionList ownerIn, IAddon addon) {
			this.owner = ownerIn;
			this.addon = addon;
			this.mc = MinecraftClient.getInstance();

			Optional<String> iconPathOptional = addon.getIcon();
			if (!iconPathOptional.isEmpty()) {
				String iconPath = iconPathOptional.get().replaceFirst("assets/", "");
				int firstDirectory = iconPath.indexOf('/');
				String modNamespace = iconPath.substring(0, firstDirectory);
				String modIconPath = iconPath.substring(firstDirectory + 1);
				this.iconIdentifier = Identifier.of(modNamespace, modIconPath);
			} else
				this.iconIdentifier = null;
		}

		@Override
		public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight,
				int mouseX, int mouseY, boolean hovered, float tickDelta) {
			// Draws the strings onto the screen.
			TextRenderer textRenderer = this.mc.textRenderer;

			drawContext.drawTexture(RenderLayer::getGuiTextured, iconIdentifier, x + 8, y + 8, 0, 0, 32, 32, 32, 32);
			drawContext.drawTextWithShadow(textRenderer, addon.getName(), (x + 54), y + 10, 16777215);
			drawContext.drawTextWithShadow(textRenderer, addon.getDescription(), (x + 54), y + 22, 16777215);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			double d0 = mouseX - (double) AddonSelectionList.this.getRowLeft();

			if (d0 < 32.0D && d0 > 16.0D) {
				this.owner.onClickEntry(this);
				return true;
			}
			this.owner.onClickEntry(this);
			return false;
		}

		@Override
		public Text getNarration() {
			return Text.of(addon.getName());
		}

		public IAddon getAddon() {
			return this.addon;
		}

		public Identifier getIcon() {
			return this.iconIdentifier;
		}
	}
}
