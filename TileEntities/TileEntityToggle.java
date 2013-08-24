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
import Reika.DragonAPI.Libraries.ReikaRedstoneHelper;
import Reika.ExpandedRedstone.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityToggle extends ExpandedRedstoneTileEntity {

	private boolean lastPower;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		if (ReikaRedstoneHelper.isPositiveEdge(world, x, y, z, lastPower)) {
			emit = !emit;
		}
		lastPower = world.isBlockIndirectlyGettingPowered(x, y, z);
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.TOGGLE.ordinal();
	}
}
