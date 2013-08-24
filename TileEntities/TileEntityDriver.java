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
import Reika.ExpandedRedstone.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityDriver extends ExpandedRedstoneTileEntity {

	private int level;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
	}

	@Override
	public boolean isBinaryRedstone() {
		return false;
	}

	public int getEmission() {
		return level;
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.DRIVER.ordinal();
	}
}
