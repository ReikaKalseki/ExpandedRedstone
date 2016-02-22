/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.ModInterface.Lua;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ExpandedRedstone.Base.AnalogWireless;
import dan200.computercraft.api.lua.LuaException;

public class LuaWirelessSetChannel extends LuaMethod {

	public LuaWirelessSetChannel() {
		super("setWirelessChannel", AnalogWireless.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
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
