/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

import net.minecraft.world.World;

public class TileEntityWeather extends TileRedstoneBase {

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		this.setEmitting(world.isRaining() || world.isThundering());
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.WEATHER.ordinal();
	}
}