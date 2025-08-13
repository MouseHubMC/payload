

package net.payload.settings.types;

import net.payload.settings.Setting;

import java.util.function.Consumer;

public class FloatSetting extends Setting<Float> {
    public final float min_value;
    public final float max_value;
    public final float step;
    private Float valueSqr;
   
    private FloatSetting(String ID, String displayName, String description, float default_value, float min_value, float max_value, float step, Consumer<Float> onUpdate) {
        super(ID, displayName, description, default_value, onUpdate);
        this.min_value = min_value;
        this.max_value = max_value;
        this.step = step;
        valueSqr = value * value;
        type = TYPE.FLOAT;
    }

    /**
     * Setter for the value. Includes rounding to the nearest "step".
     */
    @Override
    public void setValue(Float value) {
        float newValue = Math.max(min_value, Math.min(max_value, value));
        int steps = (int) Math.round((newValue) / step);
        float actualNewValue = step * steps;
        valueSqr = actualNewValue * actualNewValue;
        super.setValue(actualNewValue);
    }

    @Override
    public void silentSetValue(Float value) {
        if (isValueValid(value)) {
            this.value = value;
            this.valueSqr = value * value;
        }
    }
    
    /**
     * Checks whether or not a value is with this setting's valid range.
     */
    @Override
    protected boolean isValueValid(Float value) {
        return value >= min_value && value <= max_value;
    }
    
    public Float getValueSqr() {
    	return this.valueSqr;
    }
    
    public static BUILDER builder() {
    	return new BUILDER();
    }
    
    public static class BUILDER extends Setting.BUILDER<BUILDER, FloatSetting, Float> {
		protected Float minValue = 1f;
		protected Float maxValue = 10f;
		protected Float step = 1f;
		
		protected BUILDER() {
			super();
		}
		
		public BUILDER minValue(Float value) {
			minValue = value;
			return this;
		}
		
		public BUILDER maxValue(Float value) {
			maxValue = value;
			return this;
		}
		
		public BUILDER step(Float value) {
			step = value;
			return this;
		}
		
		@Override
		public FloatSetting build() {
			return new FloatSetting(id, displayName, description, defaultValue, minValue, maxValue, step, onUpdate);
		}
	}
}
