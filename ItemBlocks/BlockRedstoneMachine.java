/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.ItemBlocks;

import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.ExpandedRedstone.Base.BlockRedstoneBase;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneMachine extends BlockRedstoneBase {

	public BlockRedstoneMachine(Material mat) {
		super(mat);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		this.setBlockBounds(0, 0, 0, 1, 1, 1);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return ReikaAABBHelper.getBlockAABB(x, y, z);
	}

	@Override
	public int getLightOpacity(IBlockAccess iba, int x, int y, int z)
	{
		return 255;
	}

}
