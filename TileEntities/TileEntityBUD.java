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

public class TileEntityBUD extends ExpandedRedstoneTileEntity {

	private int[] IDs = new int[6];
	private int[] metas = new int[6];

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		if (this.checkForUpdates(world, x, y, z))
			this.sendPulse(20);
		this.setStates(world, x, y, z);
	}

	private void setStates(World world, int x, int y, int z) {

		for (int i = 0; i < 6; i++) {
			int dx = x+dirs[i].offsetX;
			int dy = y+dirs[i].offsetY;
			int dz = z+dirs[i].offsetZ;
			int id = world.getBlockId(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			IDs[i] = id;
			metas[i] = meta;
		}
	}

	private boolean checkForUpdates(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			int dx = x+dirs[i].offsetX;
			int dy = y+dirs[i].offsetY;
			int dz = z+dirs[i].offsetZ;
			int id = world.getBlockId(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (IDs[i] != id || false)
				return true;
		}
		return false;
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.BUD.ordinal();
	}
}
