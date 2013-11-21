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
import net.minecraft.block.BlockRedstoneLogic;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityToggle extends ExpandedRedstoneTileEntity {

	private boolean lastPower;
	private boolean lastRepeat;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		if (this.getFacing() == null)
			return;
		ForgeDirection side = this.getFacing().getOpposite();
		if (ReikaRedstoneHelper.isPositiveEdgeOnSide(world, x, y, z, lastPower, lastRepeat, side)) {
			this.toggleEmitting();
			ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.5F);
		}
		lastPower = this.wasLastPowered(world, x, y, z, side);
		lastRepeat = ReikaRedstoneHelper.isReceivingPowerFromRepeater(world, x, y, z, side);
	}

	private boolean wasLastPowered(World world, int x, int y, int z, ForgeDirection side) {
		boolean sided = world.getIndirectPowerOutput(x+side.offsetX, y+side.offsetY, z+side.offsetZ, side.getOpposite().ordinal());
		boolean repeat = false;
		int id = world.getBlockId(x+side.offsetX, y+side.offsetY, z+side.offsetZ);
		if (id != 0) {
			Block b = Block.blocksList[id];
			if (b instanceof BlockRedstoneLogic) {
				repeat = ((BlockRedstoneLogic) b).func_83011_d(world, x, y, z, side.ordinal());
			}
		}
		return (sided || repeat) && world.isBlockIndirectlyGettingPowered(x, y, z);
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
