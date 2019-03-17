/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.world.World;

import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityEmitter extends TileRedstoneBase {

	@Override
	public int getTEIndex() {
		return RedstoneTiles.EMITTER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
	}

	public boolean isBeaming() {
		return this.hasRedstoneSignal();
	}

	@Override
	public int getFrontTexture() {
		return this.isBeaming() ? 1 : 0;
	}
}
