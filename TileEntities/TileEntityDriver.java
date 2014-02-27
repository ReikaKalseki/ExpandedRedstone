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
import net.minecraft.block.BlockRedstoneLogic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityDriver extends ExpandedRedstoneTileEntity {

	private int level;

	private boolean lastPower;
	private boolean lastRepeat;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);

		if (this.getFacing() == null)
			return;
		ForgeDirection side = this.getFacing();
		if (ReikaRedstoneHelper.isPositiveEdgeOnSide(world, x, y, z, lastPower, lastRepeat, side)) {
			this.increment();
		}
		lastPower = this.wasLastPowered(world, x, y, z, side);
		lastRepeat = ReikaRedstoneHelper.isReceivingPowerFromRepeater(world, x, y, z, side);
	}

	@Override
	public boolean isBinaryRedstone() {
		return false;
	}

	@Override
	public int getEmission() {
		return level;
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.DRIVER.ordinal();
	}

	public void increment() {
		level++;
		if (level > 15)
			level = 0;
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.8F);
	}

	public void decrement() {
		level--;
		if (level < 0)
			level = 15;
		this.update();
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		level = NBT.getInteger("lvl");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("lvl", level);
	}

	@Override
	public int getTopTexture() {
		return level;
	}

	@Override
	public boolean canPowerSide(int s) {
		if (this.getFacing() == null)
			return false;
		return s == this.getFacing().ordinal();
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
}
