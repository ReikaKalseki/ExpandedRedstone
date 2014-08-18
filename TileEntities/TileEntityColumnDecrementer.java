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
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityColumnDecrementer extends TileRedstoneBase {

	@Override
	public int getTEIndex() {
		return RedstoneTiles.COLUMN.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
	}

	@Override
	public boolean canPowerSide(int s) {
		return s == this.getFacing().getOpposite().ordinal();
	}

	@Override
	public boolean isBinaryRedstone() {
		return false;
	}

	@Override
	public int getEmission() {
		int level = this.getPowerInBack();
		RedstoneTiles r = RedstoneTiles.getTEAt(worldObj, xCoord, yCoord-1, zCoord);
		int bottom = 0;
		if (r == RedstoneTiles.COLUMN) {
			bottom = ((TileEntityColumnDecrementer)this.getAdjacentTileEntity(ForgeDirection.DOWN)).getEmission()-1;
		}
		return Math.max(level, bottom);
	}

	@Override
	public boolean canProvideStrongPower() {
		return false;
	}

}
