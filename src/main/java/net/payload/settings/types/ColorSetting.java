

package net.payload.settings.types;

import java.util.function.Consumer;

import net.payload.gui.GuiManager;
import net.payload.gui.colors.Color;
import net.payload.settings.Setting;

public class ColorSetting extends Setting<Color> {
	public enum ColorMode {
	    Solid,
	    Rainbow,
	    Random,
	}

    private ColorMode mode = ColorMode.Solid;

    protected ColorSetting(String ID, String displayName, String description, Color default_value, Consumer<Color> onUpdate) {
        super(ID, displayName, description, default_value);
        type = TYPE.COLOR;
    }

    @Override
    protected boolean isValueValid(Color value) {
        return (value.getRed() <= 255 && value.getGreen() <= 255 && value.getBlue() <= 255);
    }

    public ColorMode getMode() {
    	return mode;
    }
    
    public void setMode(ColorMode color) {
        mode = color;
        switch (mode) {
            case Solid:
                this.setValue(default_value);
                break;
            case Rainbow:
                this.setValue(GuiManager.rainbowColor);
                break;
            case Random:
                this.setValue(GuiManager.randomColor);
                break;
        }
    }
    
    public static BUILDER builder() {
    	return new BUILDER();
    }
    
    public static class BUILDER extends Setting.BUILDER<BUILDER, ColorSetting, Color> {
		protected BUILDER() {
			super();
		}
		
		@Override
		public ColorSetting build() {
			return new ColorSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}
