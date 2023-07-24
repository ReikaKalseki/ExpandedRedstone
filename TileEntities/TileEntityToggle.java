/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityToggle extends TileRedstoneBase {

	private boolean lastPower;
	private boolean lastRepeat;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		ForgeDirection side = this.getFacing().getOpposite();
		if (ReikaRedstoneHelper.isPositiveEdgeOnSide(world, x, y, z, lastPower, lastRepeat, side)) {
			this.toggleEmitting();
			if (RedstoneOptions.NOISES.getState())
				ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.5F);
		}
		lastPower = ReikaRedstoneHelper.isPoweredOnSide(world, x, y, z, side);
		lastRepeat = ReikaRedstoneHelper.isReceivingPowerFromRepeater(world, x, y, z, side);
	}

	@Override
	public RedstoneTiles getTile() {
		return RedstoneTiles.TOGGLE;
	}

	@Override
	public boolean canPowerSide(int s) {
		if (this.getFacing() == null)
			return false;
		return s == this.getFacing().getOpposite().ordinal();
	}

	@Override
	public int getTopTexture() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		ForgeDirection side = this.getFacing().getOpposite();
		boolean power = world.isBlockIndirectlyGettingPowered(x+side.offsetX, y+side.offsetY, z+side.offsetZ);
		if (!this.isEmitting() && !power)
			return 0;
		if (!this.isEmitting() && power)
			return 3;
		if (this.isEmitting() && !power)
			return 1;
		if (this.isEmitting() && power)
			return 2;
		return 0;
	}
}
