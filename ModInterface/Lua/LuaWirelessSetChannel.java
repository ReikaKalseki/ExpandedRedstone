/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.ModInterface.Lua;

import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ExpandedRedstone.Base.AnalogWireless;

public class LuaWirelessSetChannel extends LuaMethod {

	public LuaWirelessSetChannel() {
		super("setWirelessChannel", AnalogWireless.class);
	}

	@Override
	protected Object[] invoke(TileEntity te, Object[] args) throws LuaMethodException, InterruptedException {
		((AnalogWireless)te).setChannel((Integer)args[0]);
		return null;
	}

	@Override
	public String getDocumentation() {
		return "Selects a given channel.";
	}

	@Override
	public String getArgsAsString() {
		return "int channel";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.VOID;
	}

}
