/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.world.World;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityWeather extends ExpandedRedstoneTileEntity {

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
