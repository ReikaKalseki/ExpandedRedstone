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

import Reika.DragonAPI.ModInteract.Lua.LuaMethod;


public class RedstoneLuaMethods {

	private static final LuaMethod getChannel = new LuaWirelessGetChannel();
	private static final LuaMethod setChannel = new LuaWirelessSetChannel();
	private static final LuaMethod setTiming = new Lua555SetTiming();
	private static final LuaMethod getWeather = new LuaRedstoneGetWeather();
	private static final LuaMethod setDriver = new LuaSetSignalDriver();

}
