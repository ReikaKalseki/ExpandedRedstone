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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityWeather extends TileRedstoneBase {

	private int level;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		level = this.getLevel(world, x, y, z);
	}

	@Override
	public boolean isBinaryRedstone() {
		return false;
	}

	@Override
	public int getEmission() {
		return level;
	}

	public int getLevel(World world, int x, int y, int z) {
		if (!world.canBlockSeeTheSky(x, y+1, z))
			return 0;
		if (world.isThundering())
			return 15;
		else if (world.isRaining())
			return 7;
		else
			return 0;
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.WEATHER.ordinal();
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		level = NBT.getInteger("lvl");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("lvl", level);
	}
}
