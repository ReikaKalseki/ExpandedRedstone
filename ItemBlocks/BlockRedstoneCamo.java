/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.ItemBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.ExpandedRedstone.Base.BlockRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import Reika.ExpandedRedstone.TileEntities.TileEntityCamo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRedstoneCamo extends BlockRedstoneBase {

	public BlockRedstoneCamo(Material mat) {
		super(mat);
	}

	@Override
	public final boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isBlockNormalCube() {
		return false;
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		int meta = iba.getBlockMetadata(x, y, z);
		RedstoneTiles r = RedstoneTiles.getTEAt(iba, x, y, z);
		TileEntityCamo tc = (TileEntityCamo)iba.getTileEntity(x, y, z);
		AxisAlignedBB box = tc.getBoundingBox();
		if (box == null)
			this.setBlockBounds(0, 0, 0, 0, 0, 0);
		else
			this.setBlockBounds((float)box.minX, (float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.maxY, (float)box.maxZ);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		Block id = world.getBlock(x, y, z);
		if (id != this)
			return id == Blocks.air ? null : id.getCollisionBoundingBoxFromPool(world, x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
		TileEntityCamo tc = (TileEntityCamo)world.getTileEntity(x, y, z);
		AxisAlignedBB box = tc.getBoundingBox();
		if (box == null)
			return null;
		else
			return box.offset(x, y, z);
	}

	@Override
	public int getLightOpacity(IBlockAccess iba, int x, int y, int z)
	{
		RedstoneTiles r = RedstoneTiles.getTEAt(iba, x, y, z);
		World world = iba.getTileEntity(x, y, z).worldObj;
		boolean pwr = world.isBlockIndirectlyGettingPowered(x, y, z);
		Block idb = world.getBlock(x, y-1, z);
		if (idb == Blocks.air)
			return 0;
		return pwr ? idb.getLightOpacity(world, x, y-1, z) : 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		TileEntityCamo te = (TileEntityCamo)world.getTileEntity(x, y, z);
		if (te.isOverridingIcon(0)) {
			BlockKey b = te.getImitatedBlockID();
			if (b != null && b.blockID != Blocks.air) {
				return b.blockID.colorMultiplier(world, x, y-1, z);
			}
		}
		return super.colorMultiplier(world, x, y, z);
	}
	/*
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler config) {
		World world = acc.getWorld();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
			if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
				TileEntityCamo te = (TileEntityCamo)acc.getTileEntity();
				Block id = te.getImitatedBlockID();
				if (id != Blocks.air) {
					if (id != null) {
						return new ItemStack(id, 1, world.getBlockMetadata(x, y-1, z));
					}
				}
			}
		}
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		World world = acc.getWorld();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
				TileEntityCamo te = (TileEntityCamo)acc.getTileEntity();
				Block b = te.getImitatedBlockID();
				if (b != Blocks.air) {
					if (b != null) {
						ItemStack mimic = new ItemStack(b, 1, world.getBlockMetadata(x, y-1, z));
						return ReikaJavaLibrary.makeListFrom(EnumChatFormatting.WHITE+mimic.getDisplayName());
					}
				}
				return new ArrayList();
			}
			else {
				tip.add(EnumChatFormatting.WHITE+this.getPickBlock(mov, world, x, y, z).getDisplayName());
			}
		}
		return tip;
	}

	@Override
	public List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}*/

}
