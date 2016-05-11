/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.ModInterface.Lua;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ExpandedRedstone.TileEntities.TileEntityDriver;
import dan200.computercraft.api.lua.LuaException;

public class LuaSetSignalDriver extends LuaMethod {

	public LuaSetSignalDriver() {
		super("setSignalLevel", TileEntityDriver.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		((TileEntityDriver)te).setOutput((Integer)args[0]);
		return null;
	}

	@Override
	public String getDocumentation() {
		return "Selects a given signal strength.";
	}

	@Override
	public String getArgsAsString() {
		return "int level";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.VOID;
	}

}
