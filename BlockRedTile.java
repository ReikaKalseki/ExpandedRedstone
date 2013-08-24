/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class BlockRedTile extends Block {

	public BlockRedTile(int ID, Material mat) {
		super(ID, mat);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return RedstoneTiles.createTEFromMetadata(meta);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess iba, int x, int y, int z, int s)
	{
		ExpandedRedstoneTileEntity te = (ExpandedRedstoneTileEntity)iba.getBlockTileEntity(x, y, z);
		if (te.isBinaryRedstone())
			return te.isEmitting() ? 15 : 0;
		else
			return te.getEmission();
	}

	@Override
	public final boolean canBeReplacedByLeaves(World world, int x, int y, int z)
	{
		return false;
	}

	@Override
	public final boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata)
	{
		return false;
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face)
	{
		return 0;
	}

	@Override
	public boolean canDragonDestroy(World world, int x, int y, int z)
	{
		return true;
	}

	@Override
	public final boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public final boolean canProvidePower()
	{
		return true;
	}

}
