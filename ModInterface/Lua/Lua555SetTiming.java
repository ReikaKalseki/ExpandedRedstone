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
import Reika.ExpandedRedstone.TileEntities.TileEntity555;
import Reika.ExpandedRedstone.TileEntities.TileEntity555.Settings;

public class Lua555SetTiming extends LuaMethod {

	public Lua555SetTiming() {
		super("setTiming", TileEntity555.class);
	}

	@Override
	protected Object[] invoke(TileEntity te, Object[] args) throws LuaMethodException, InterruptedException {
		TileEntity555 t5 = (TileEntity555)te;
		t5.loadSetting(Settings.list[(int)args[0]], true, false);
		t5.loadSetting(Settings.list[(int)args[1]], false, false);
		return null;
	}

	@Override
	public String getDocumentation() {
		return "Selects the timing settings.";
	}

	@Override
	public String getArgsAsString() {
		return "int highTime, int lowTime";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.VOID;
	}

}
