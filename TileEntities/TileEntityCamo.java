/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ExpandedRedstone.Base.BlockRedstoneBase;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityCamo extends TileRedstoneBase {

	@Override
	public int getTEIndex() {
		return RedstoneTiles.CAMOFLAGE.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		this.setEmitting(world.isBlockIndirectlyGettingPowered(x, y, z));
		//ReikaJavaLibrary.pConsoleIf(this.getImitatedBlockID(), yCoord == 64);
	}

	@Override
	public boolean isOverridingIcon(int side) {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}

	@Override
	public IIcon getOverridingIcon(int side) {
		Block id = worldObj.getBlock(xCoord, yCoord-1, zCoord);
		int meta = worldObj.getBlockMetadata(xCoord, yCoord-1, zCoord);
		if (id == Blocks.air)
			return BlockRedstoneBase.trans;
		if (id == this.getTileEntityBlockID() && meta == this.getTEIndex()) {
			TileEntityCamo te = (TileEntityCamo)worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
			if (te.isOverridingIcon(side)) {
				IIcon ico = te.getOverridingIcon(side);
				if (ico == Blocks.grass.getIcon(side, meta) && !this.canRenderAsGrass())
					ico = Blocks.dirt.getIcon(side, meta);
				else if (te.getImitatedBlockID() == Blocks.grass && ico == Blocks.dirt.getIcon(side, meta) && this.canRenderAsGrass())
					ico = Blocks.grass.getIcon(side, meta);
				return ico;
			}
		}
		IIcon ico = id.getIcon(side, meta);
		if (ico == Blocks.grass.getIcon(side, meta) && !this.canRenderAsGrass())
			ico = Blocks.dirt.getIcon(side, meta);
		return ico;
	}

	public Block getImitatedBlockID() {
		if (!this.isOverridingIcon(0))
			return null;
		else {
			Block id = worldObj.getBlock(xCoord, yCoord-1, zCoord);
			int meta = worldObj.getBlockMetadata(xCoord, yCoord-1, zCoord);
			if (id == this.getTileEntityBlockID() && meta == this.getTEIndex()) {
				TileEntityCamo co = (TileEntityCamo)worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
				return co.getImitatedBlockID();
			}
			else
				return id;
		}
	}

	public AxisAlignedBB getBoundingBox() {
		if (!this.isOverridingIcon(0))
			return AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);
		Block b = worldObj.getBlock(xCoord, yCoord-1, zCoord);
		int meta = worldObj.getBlockMetadata(xCoord, yCoord-1, zCoord);
		if (b == Blocks.air)
			return null;
		if (b == this.getTileEntityBlockID() && meta == this.getTEIndex()) {
			TileEntityCamo te = (TileEntityCamo)worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
			if (te.isOverridingIcon(0))
				return te.getBoundingBox();
		}
		double minx = b.getBlockBoundsMinX();
		double miny = b.getBlockBoundsMinY();
		double minz = b.getBlockBoundsMinZ();
		double maxx = b.getBlockBoundsMaxX();
		double maxy = b.getBlockBoundsMaxY();
		double maxz = b.getBlockBoundsMaxZ();
		if (b instanceof BlockLiquid) {
			maxy = 1-BlockLiquid.getLiquidHeightPercent(meta);
		}
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(minx, miny, minz, maxx, maxy, maxz);
		//return Blocks.blocksList[id].getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord-1, zCoord);
		return box;
	}

	//ReikaJavaLibrary.pConsoleIf(this.canRenderAsGrass(), yCoord == 64);
	public boolean canRenderAsGrass() {
		Block id = worldObj.getBlock(xCoord, yCoord+1, zCoord);
		int meta = worldObj.getBlockMetadata(xCoord, yCoord+1, zCoord);
		if (id == Blocks.air)
			return true;
		if (id == this.getTileEntityBlockID()) {
			if (meta == this.getTEIndex()) {
				TileEntityCamo co = (TileEntityCamo)worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
				if (co.isOverridingIcon(0)) {
					Block im = co.getImitatedBlockID();
					if (im == Blocks.air || im == null)
						return true;
					return im.getCanBlockGrass();
				}
				else
					return true;
			}
			else
				return true;
		}
		return id.getCanBlockGrass();
	}

	@Override
	public boolean canPowerSide(int s) {
		return s == ForgeDirection.DOWN.ordinal() && RedstoneTiles.getTEAt(worldObj, xCoord, yCoord+1, zCoord) == RedstoneTiles.CAMOFLAGE;
	}

	@Override
	public boolean canProvideStrongPower() {
		return false;
	}

}
