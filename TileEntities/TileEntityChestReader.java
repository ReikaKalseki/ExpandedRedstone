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

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.ExpandedRedstone.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityChestReader extends ExpandedRedstoneTileEntity {

	private boolean signalFull = true;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		this.readChest(world);
	}

	private void readChest(World world) {
		TileEntity te = world.getBlockTileEntity(this.getFacingX(), this.getFacingY(), this.getFacingZ());
		if (te instanceof IInventory) {
			if (signalFull)
				emit = ReikaInventoryHelper.isFull((IInventory)te);
			else
				emit = ReikaInventoryHelper.isEmpty((IInventory)te);
		}
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.CHESTREADER.ordinal();
	}

	public void alternate() {
		signalFull = !signalFull;
	}
}
