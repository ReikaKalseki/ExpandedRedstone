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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntitySignalScaler extends TileRedstoneBase {

	private int minScale;
	private int maxScale;

	@Override
	public int getTEIndex() {
		return RedstoneTiles.SCALER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
	}

	@Override
	public boolean canPowerSide(int s) {
		return s == this.getFacing().getOpposite().ordinal();
	}

	@Override
	public boolean isBinaryRedstone() {
		return false;
	}

	@Override
	public int getEmission() {
		int in = this.getPowerInBack();
		return this.getOutputFromInput(in);
	}

	public int getMinValue() {
		return minScale;
	}

	public int getMaxValue() {
		return maxScale;
	}

	public int getBandwidth() {
		return maxScale-minScale;
	}

	public int getOutputFromInput(int in) {
		return minScale+in*this.getBandwidth()/15;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		minScale = NBT.getInteger("min");
		maxScale = NBT.getInteger("max");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("min", minScale);
		NBT.setInteger("max", maxScale);
	}

	public void incrementMinValue() {
		minScale++;
		if (minScale > 15)
			minScale = 0;
		ReikaChatHelper.clearChat();
		ReikaChatHelper.write("Minimum value set to "+minScale+".");
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.8F);
	}

	public void incrementMaxValue() {
		maxScale++;
		if (maxScale > 15)
			maxScale = 0;
		ReikaChatHelper.clearChat();
		ReikaChatHelper.write("Maximum value set to "+maxScale+".");
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.8F);
	}

}
