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

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityCamo extends ExpandedRedstoneTileEntity {

	@Override
	public int getTEIndex() {
		return RedstoneTiles.CAMOFLAGE.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
	}

	@Override
	public boolean isOverridingIcon(int side) {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}

	@Override
	public Icon getOverridingIcon(int side) {
		int id = worldObj.getBlockId(xCoord, yCoord-1, zCoord);
		int meta = worldObj.getBlockMetadata(xCoord, yCoord-1, zCoord);
		if (id == 0)
			return null;
		if (id == this.getTileEntityBlockID() && meta == this.getTEIndex()) {
			TileEntityCamo te = (TileEntityCamo)worldObj.getBlockTileEntity(xCoord, yCoord-1, zCoord);
			if (te.isOverridingIcon(side))
				return te.getOverridingIcon(side);
		}
		return Block.blocksList[id].getIcon(side, meta);
	}

	public AxisAlignedBB getBoundingBox() {
		if (!this.isOverridingIcon(0))
			return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 1, 1, 1);
		int id = worldObj.getBlockId(xCoord, yCoord-1, zCoord);
		int meta = worldObj.getBlockMetadata(xCoord, yCoord-1, zCoord);
		if (id == 0)
			return null;
		if (id == this.getTileEntityBlockID() && meta == this.getTEIndex()) {
			TileEntityCamo te = (TileEntityCamo)worldObj.getBlockTileEntity(xCoord, yCoord-1, zCoord);
			if (te.isOverridingIcon(0))
				return te.getBoundingBox();
		}
		Block b = Block.blocksList[id];
		double minx = b.getBlockBoundsMinX();
		double miny = b.getBlockBoundsMinY();
		double minz = b.getBlockBoundsMinZ();
		double maxx = b.getBlockBoundsMaxX();
		double maxy = b.getBlockBoundsMaxY();
		double maxz = b.getBlockBoundsMaxZ();
		if (b instanceof BlockFluid) {
			maxy = 1-BlockFluid.getFluidHeightPercent(meta);
		}
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(minx, miny, minz, maxx, maxy, maxz);
		//return Block.blocksList[id].getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord-1, zCoord);
		return box;
	}

}
