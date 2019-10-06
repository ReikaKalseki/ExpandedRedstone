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
import Reika.ExpandedRedstone.TileEntities.TileEntityWeather;

public class LuaRedstoneGetWeather extends LuaMethod {

	public LuaRedstoneGetWeather() {
		super("getWeatherState", TileEntityWeather.class);
	}

	@Override
	protected Object[] invoke(TileEntity te, Object[] args) throws LuaMethodException, InterruptedException {
		return new Object[]{((TileEntityWeather)te).getEmission()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the weather state.";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.INTEGER;
	}

}
