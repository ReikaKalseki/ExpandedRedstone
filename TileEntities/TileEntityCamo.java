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
import net.minecraftforge.common.ForgeDirection;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.ItemBlocks.BlockRedTile;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityCamo extends ExpandedRedstoneTileEntity {

	@Override
	public int getTEIndex() {
		return RedstoneTiles.CAMOFLAGE.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		emit = world.isBlockIndirectlyGettingPowered(x, y, z);
		//ReikaJavaLibrary.pConsoleIf(this.getImitatedBlockID(), yCoord == 64);
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
			return BlockRedTile.trans;
		if (id == this.getTileEntityBlockID() && meta == this.getTEIndex()) {
			TileEntityCamo te = (TileEntityCamo)worldObj.getBlockTileEntity(xCoord, yCoord-1, zCoord);
			if (te.isOverridingIcon(side)) {
				Icon ico = te.getOverridingIcon(side);
				if (ico == Block.grass.getIcon(side, meta) && !this.canRenderAsGrass())
					ico = Block.dirt.getIcon(side, meta);
				else if (te.getImitatedBlockID() == Block.grass.blockID && ico == Block.dirt.getIcon(side, meta) && this.canRenderAsGrass())
					ico = Block.grass.getIcon(side, meta);
				return ico;
			}
		}
		Icon ico = Block.blocksList[id].getIcon(side, meta);
		if (ico == Block.grass.getIcon(side, meta) && !this.canRenderAsGrass())
			ico = Block.dirt.getIcon(side, meta);
		return ico;
	}

	public int getImitatedBlockID() {
		if (!this.isOverridingIcon(0))
			return -1;
		else {
			int id = worldObj.getBlockId(xCoord, yCoord-1, zCoord);
			int meta = worldObj.getBlockMetadata(xCoord, yCoord-1, zCoord);
			if (id == this.getTileEntityBlockID() && meta == this.getTEIndex()) {
				TileEntityCamo co = (TileEntityCamo)worldObj.getBlockTileEntity(xCoord, yCoord-1, zCoord);
				return co.getImitatedBlockID();
			}
			else
				return id;
		}
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

	//ReikaJavaLibrary.pConsoleIf(this.canRenderAsGrass(), yCoord == 64);
	public boolean canRenderAsGrass() {
		int id = worldObj.getBlockId(xCoord, yCoord+1, zCoord);
		int meta = worldObj.getBlockMetadata(xCoord, yCoord+1, zCoord);
		if (id == 0)
			return true;
		if (id == this.getTileEntityBlockID()) {
			if (meta == this.getTEIndex()) {
				TileEntityCamo co = (TileEntityCamo)worldObj.getBlockTileEntity(xCoord, yCoord+1, zCoord);
				if (co.isOverridingIcon(0)) {
					int im = co.getImitatedBlockID();
					if (im == -1)
						return true;
					return Block.canBlockGrass[im];
				}
				else
					return true;
			}
			else
				return true;
		}
		return Block.canBlockGrass[id];
	}

	@Override
	public boolean canPowerSide(int s) {
		return s == ForgeDirection.DOWN.ordinal();
	}

}
