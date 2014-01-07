/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.Registry;

import net.minecraftforge.common.Configuration;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.ConfigList;
import Reika.ExpandedRedstone.ExpandedRedstone;

public enum RedstoneOptions implements ConfigList {

	LOGLOADING("Console Loading Info", true),
	DEBUGMODE("Debug Mode", false),
	NOISES("Ticking Noises", true);

	private String label;
	private boolean defaultState;
	private int defaultValue;
	private float defaultFloat;
	private Class type;

	public static final RedstoneOptions[] optionList = RedstoneOptions.values();

	private RedstoneOptions(String l, boolean d) {
		label = l;
		defaultState = d;
		type = boolean.class;
	}

	private RedstoneOptions(String l, int d) {
		label = l;
		defaultValue = d;
		type = int.class;
	}

	public boolean isBoolean() {
		return type == boolean.class;
	}

	public boolean isNumeric() {
		return type == int.class;
	}

	public boolean isDecimal() {
		return type == float.class;
	}

	public float setDecimal(Configuration config) {
		if (!this.isDecimal())
			throw new RegistrationException(ExpandedRedstone.instance, "Config Property \""+this.getLabel()+"\" is not decimal!");
		return (float)config.get("Control Setup", this.getLabel(), defaultFloat).getDouble(defaultFloat);
	}

	public float getFloat() {
		return (Float)ExpandedRedstone.config.getControl(this.ordinal());
	}

	public Class getPropertyType() {
		return type;
	}

	public int setValue(Configuration config) {
		if (!this.isNumeric())
			throw new RegistrationException(ExpandedRedstone.instance, "Config Property \""+this.getLabel()+"\" is not numerical!");
		return config.get("Control Setup", this.getLabel(), defaultValue).getInt();
	}

	public String getLabel() {
		return label;
	}

	public boolean setState(Configuration config) {
		if (!this.isBoolean())
			throw new RegistrationException(ExpandedRedstone.instance, "Config Property \""+this.getLabel()+"\" is not boolean!");
		return config.get("Control Setup", this.getLabel(), defaultState).getBoolean(defaultState);
	}

	public boolean getState() {
		return (Boolean)ExpandedRedstone.config.getControl(this.ordinal());
	}

	public int getValue() {
		return (Integer)ExpandedRedstone.config.getControl(this.ordinal());
	}

	public boolean isDummiedOut() {
		return type == null;
	}
}
