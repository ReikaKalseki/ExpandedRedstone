/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.init.Blocks;
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
		lastPower = this.wasLastPowered(world, x, y, z, side);
		lastRepeat = ReikaRedstoneHelper.isReceivingPowerFromRepeater(world, x, y, z, side);
	}

	private boolean wasLastPowered(World world, int x, int y, int z, ForgeDirection side) {
		boolean sided = world.getIndirectPowerOutput(x+side.offsetX, y+side.offsetY, z+side.offsetZ, side.getOpposite().ordinal());
		boolean repeat = false;
		Block b = world.getBlock(x+side.offsetX, y+side.offsetY, z+side.offsetZ);
		if (b != Blocks.air) {
			if (b instanceof BlockRedstoneDiode) {
				repeat = ((BlockRedstoneDiode) b).func_149912_i(world, x, y, z, side.ordinal());
			}
		}
		return (sided || repeat) && this.hasRedstoneSignal();
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.TOGGLE.ordinal();
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
