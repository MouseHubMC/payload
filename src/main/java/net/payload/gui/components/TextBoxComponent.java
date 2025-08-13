

package net.payload.gui.components;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import net.payload.Payload;
import net.payload.event.events.KeyDownEvent;
import net.payload.event.events.MouseClickEvent;
import net.payload.event.listeners.KeyDownListener;
import net.payload.gui.GuiManager;
import net.payload.gui.Margin;
import net.payload.gui.Size;
import net.payload.gui.colors.Color;
import net.payload.settings.types.StringSetting;
import net.payload.utils.render.Render2D;
import net.payload.utils.types.MouseAction;
import net.payload.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

public class TextBoxComponent extends Component implements KeyDownListener {
	private boolean listeningForKey;

	@Nullable
	private String text;
	private StringSetting stringSetting;

	private boolean isFocused = false;
	private float focusAnimationProgress = 0.0f;
	private Color errorBorderColor = new Color(255, 0, 0);
	private boolean isErrorState = false;

	// Events
	private Consumer<String> onTextChanged;

	public TextBoxComponent() {
		super();
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
		this.text = "";
	}

	public TextBoxComponent(String text) {
		super();
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
		this.text = text;
	}

	public TextBoxComponent(StringSetting stringSetting) {
		super();
		this.setMargin(new Margin(8f, 2f, 8f, 2f));

		this.stringSetting = stringSetting;
		this.stringSetting.addOnUpdate(s -> {
			this.text = s;
		});

		this.header = stringSetting.displayName;
		this.text = stringSetting.getValue();
	}

	@Override
	public void measure(Size availableSize) {
		preferredSize = new Size(availableSize.getWidth(), 30.0f);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();
		float actualHeight = this.getActualSize().getHeight();

		if (isFocused) {
			focusAnimationProgress = Math.min(1.0f, focusAnimationProgress + partialTicks * 0.1f);
		} else {
			focusAnimationProgress = Math.max(0.0f, focusAnimationProgress - partialTicks * 0.1f);
		}

		Color startColor = new Color(182, 220, 255, 155); // Start color of gradient
		Color endColor = new Color(185, 182, 229, 155); // End color of gradient


// Draw the button with a gradient background
		Render2D.drawHorizontalGradient(matrix4f, actualX, actualY, actualWidth, actualHeight, startColor, endColor);

// Optionally, draw the outline around the button (with rounded corners)
		Render2D.drawBoxOutline(matrix4f, actualX, actualY, actualWidth, actualHeight,
				GuiManager.borderColor.getValue());

		if (text != null && !text.isEmpty()) {
			int visibleStringLength = (int) (actualWidth - 16 / 10);

			int visibleStringIndex = Math.min(Math.max(0, text.length() - visibleStringLength - 1), text.length() - 1);
			String visibleString = text.substring(visibleStringIndex, text.length());
			Render2D.drawString(drawContext, visibleString, actualX + 8, actualY + 8, 0xFFFFFF);
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered) {
				setListeningForKey(true);
				event.cancel();
			} else {
				setListeningForKey(false);
			}
		}

		isFocused = listeningForKey;
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (listeningForKey) {
			int key = event.GetKey();

			if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
				setListeningForKey(false);
			} else if (key == GLFW.GLFW_KEY_BACKSPACE) {
				if (!text.isEmpty()) {
					text = text.substring(0, text.length() - 1);
					if (stringSetting != null)
						stringSetting.setValue(text);
				}
			} else if (keyIsValid(key) || key == GLFW.GLFW_KEY_SPACE) {
				char keyCode = (char) key;

				if (key != GLFW.GLFW_KEY_SPACE && !Screen.hasShiftDown())
					keyCode = Character.toLowerCase(keyCode);

				text += "" + keyCode;
				if (stringSetting != null)
					stringSetting.setValue(text);
			}

			event.cancel();
		}
	}

	private boolean keyIsValid(int key) {
		return (key >= 48 && key <= 57) || (key >= 65 && key <= 90) || (key >= 97 && key <= 122);
	}

	public String getText() {
		return text;
	}

	public void setText(String newText) {
		text = newText;
		if (stringSetting != null)
			stringSetting.setValue(newText);
	}

	public void setErrorState(boolean isError) {
		this.isErrorState = isError;
	}

	private void setListeningForKey(boolean state) {
		listeningForKey = state;
		if (listeningForKey) {
			Payload.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		} else {
			Payload.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
			if (onTextChanged != null) {
				onTextChanged.accept(text);
			}
		}
	}

	public void setOnTextChanged(Consumer<String> onTextChanged) {
		this.onTextChanged = onTextChanged;
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		setListeningForKey(false);
	}
}
