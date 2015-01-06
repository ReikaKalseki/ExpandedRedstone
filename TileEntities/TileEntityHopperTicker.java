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
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityHopperTicker extends TileRedstoneBase {

	private boolean lastPower;

	@Override
	public int getTEIndex() {
		return RedstoneTiles.HOPPER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		int dx = this.getFacingX();
		int dy = this.getFacingY();
		int dz = this.getFacingZ();
		Block b = world.getBlock(dx, dy, dz);
		if (ReikaRedstoneHelper.isPositiveEdge(world, x, y, z, lastPower)) {
			if (b == Blocks.hopper) {
				TileEntityHopper te = (TileEntityHopper)world.getTileEntity(dx, dy, dz);
				te.func_145896_c(0);
				te.func_145887_i();
				te.func_145896_c(0);
				boolean red = world.isBlockIndirectlyGettingPowered(dx, dy, dz);
				if (RedstoneOptions.NOISES.getState())
					ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.click", 0.4F, red ? 0.25F : 0.8F);
			}
		}
		lastPower = world.isBlockIndirectlyGettingPowered(x, y, z);
	}

}
