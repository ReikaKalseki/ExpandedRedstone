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

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityHopperTicker extends ExpandedRedstoneTileEntity {

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
		int id = world.getBlockId(dx, dy, dz);
		if (ReikaRedstoneHelper.isPositiveEdge(world, x, y, z, lastPower)) {
			if (id == Block.hopperBlock.blockID) {
				TileEntityHopper te = (TileEntityHopper)world.getBlockTileEntity(dx, dy, dz);
				te.setTransferCooldown(0);
				te.updateHopper();
				te.setTransferCooldown(0);
				boolean red = world.isBlockIndirectlyGettingPowered(dx, dy, dz);
				if (RedstoneOptions.NOISES.getState())
					ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.click", 0.4F, red ? 0.25F : 0.8F);
			}
		}
		lastPower = world.isBlockIndirectlyGettingPowered(x, y, z);
	}

}
