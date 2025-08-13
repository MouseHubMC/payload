

/**
 * TileBreaker Module
 */
package net.payload.module.modules.world;

import java.util.ArrayList;

import net.payload.Payload;
import net.payload.event.events.Render3DEvent;
import net.payload.event.events.TickEvent.Post;
import net.payload.event.events.TickEvent.Pre;
import net.payload.event.listeners.Render3DListener;
import net.payload.event.listeners.TickListener;
import net.payload.gui.colors.Color;
import net.payload.module.Category;
import net.payload.module.Module;
import net.payload.settings.types.ColorSetting;
import net.payload.settings.types.FloatSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.payload.utils.render.Render3D;

public class TileBreaker extends Module implements TickListener, Render3DListener {
	private ArrayList<Block> blocks = new ArrayList<Block>();

	private FloatSetting radius = FloatSetting.builder().id("tilebreaker_radius").displayName("Radius")
			.description("Radius").defaultValue(5f).minValue(0f).maxValue(15f).step(1f).build();

	private ColorSetting color = ColorSetting.builder().id("tilebreaker_color").displayName("Color")
			.description("Color").defaultValue(new Color(0f, 1f, 1f)).build();

	public TileBreaker() {
		super("TileBreaker");
		this.setCategory(Category.of("World"));
		this.setDescription("Destroys blocks that can be instantly broken around the player");

		this.loadTileBreakerBlocks();

		this.addSetting(color);
		this.addSetting(radius);
	}

	public void setRadius(int radius) {
		this.radius.setValue((float) radius);
	}

	@Override
	public void onDisable() {
		Payload.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Payload.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Payload.getInstance().eventManager.AddListener(Render3DListener.class, this);
		Payload.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {
	}

	private void loadTileBreakerBlocks() {
		this.blocks.add(Blocks.TORCH);
		this.blocks.add(Blocks.WALL_TORCH);
		this.blocks.add(Blocks.REDSTONE_TORCH);
		this.blocks.add(Blocks.REDSTONE_WALL_TORCH);
		this.blocks.add(Blocks.FERN);
		this.blocks.add(Blocks.LARGE_FERN);
		this.blocks.add(Blocks.FLOWER_POT);
		this.blocks.add(Blocks.POTATOES);
		this.blocks.add(Blocks.CARROTS);
		this.blocks.add(Blocks.WHEAT);
		this.blocks.add(Blocks.BEETROOTS);
		this.blocks.add(Blocks.SUGAR_CANE);
		this.blocks.add(Blocks.GRASS_BLOCK);
		this.blocks.add(Blocks.TALL_GRASS);
		this.blocks.add(Blocks.SEAGRASS);
		this.blocks.add(Blocks.TALL_SEAGRASS);
		this.blocks.add(Blocks.DEAD_BUSH);
		this.blocks.add(Blocks.DANDELION);
		this.blocks.add(Blocks.ROSE_BUSH);
		this.blocks.add(Blocks.POPPY);
		this.blocks.add(Blocks.BLUE_ORCHID);
		this.blocks.add(Blocks.ALLIUM);
		this.blocks.add(Blocks.AZURE_BLUET);
		this.blocks.add(Blocks.RED_TULIP);
		this.blocks.add(Blocks.ORANGE_TULIP);
		this.blocks.add(Blocks.WHITE_TULIP);
		this.blocks.add(Blocks.PINK_TULIP);
		this.blocks.add(Blocks.OXEYE_DAISY);
		this.blocks.add(Blocks.CORNFLOWER);
		this.blocks.add(Blocks.WITHER_ROSE);
		this.blocks.add(Blocks.LILY_OF_THE_VALLEY);
		this.blocks.add(Blocks.BROWN_MUSHROOM);
		this.blocks.add(Blocks.RED_MUSHROOM);
		this.blocks.add(Blocks.SUNFLOWER);
		this.blocks.add(Blocks.LILAC);
		this.blocks.add(Blocks.PEONY);
	}

	public boolean isTileBreakerBlock(Block b) {
		return this.blocks.contains(b);
	}

	@Override
	public void onRender(Render3DEvent event) {
		int rad = this.radius.getValue().intValue();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos((int) MC.player.getBlockX() + x, MC.player.getBlockY() + y,
							MC.player.getBlockZ() + z);
					Block block = MC.world.getBlockState(blockpos).getBlock();
					if (this.isTileBreakerBlock(block)) {
						Render3D.draw3DBox(event.GetMatrix(), event.getCamera(), new Box(blockpos), color.getValue(), 1.0f);
					}
				}
			}
		}
	}

	@Override
	public void onTick(Pre event) {
		int rad = this.radius.getValue().intValue();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos(MC.player.getBlockX() + x, MC.player.getBlockY() + y,
							MC.player.getBlockZ() + z);
					Block block = MC.world.getBlockState(blockpos).getBlock();
					if (this.isTileBreakerBlock(block)) {
						MC.player.networkHandler.sendPacket(
								new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, blockpos, Direction.NORTH));
						MC.player.networkHandler.sendPacket(
								new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, blockpos, Direction.NORTH));
					}
				}
			}
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
