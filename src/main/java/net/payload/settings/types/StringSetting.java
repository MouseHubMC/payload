

package net.payload.settings.types;

import java.util.function.Consumer;

import net.payload.settings.Setting;

public class StringSetting extends Setting<String> {
	protected StringSetting(String ID, String displayName, String description, String default_value,
			Consumer<String> onUpdate) {
		super(ID, description, default_value, onUpdate);
		type = TYPE.STRING;
	}

	/**
	 * Checks whether or not a value is with this setting's valid range.
	 */
	@Override
	protected boolean isValueValid(String value) {
		return true;
	}

	public static BUILDER builder() {
		return new BUILDER();
	}

	public static class BUILDER extends Setting.BUILDER<BUILDER, StringSetting, String> {
		protected BUILDER() {
			super();
		}

		@Override
		public StringSetting build() {
			return new StringSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}
