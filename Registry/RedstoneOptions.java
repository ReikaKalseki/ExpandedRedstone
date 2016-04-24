/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.Registry;

import Reika.DragonAPI.Interfaces.Configuration.BooleanConfig;
import Reika.DragonAPI.Interfaces.Configuration.UserSpecificConfig;
import Reika.ExpandedRedstone.ExpandedRedstone;

public enum RedstoneOptions implements BooleanConfig, UserSpecificConfig {

	NOISES("Ticking Noises", true);

	private String label;
	private boolean defaultState;
	private Class type;

	public static final RedstoneOptions[] optionList = RedstoneOptions.values();

	private RedstoneOptions(String l, boolean d) {
		label = l;
		defaultState = d;
		type = boolean.class;
	}

	public boolean isBoolean() {
		return type == boolean.class;
	}

	public Class getPropertyType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public boolean getState() {
		return (Boolean)ExpandedRedstone.config.getControl(this.ordinal());
	}

	public boolean isDummiedOut() {
		return type == null;
	}

	@Override
	public boolean getDefaultState() {
		return defaultState;
	}

	@Override
	public boolean isEnforcingDefaults() {
		return false;
	}

	@Override
	public boolean shouldLoad() {
		return true;
	}

	@Override
	public boolean isUserSpecific() {
		switch(this) {
			case NOISES:
				return true;
			default:
				return false;
		}
	}
}
