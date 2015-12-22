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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;

public class TileEntityBlockReader extends TileRedstoneBase {

	private static enum ReadMode {
		BLOCK(),
		METADATA(),
		TILEENTITY();

		private static ReadMode[] list = values();

		public ReadMode next() {
			return this.ordinal() == list.length-1 ? list[0] : list[this.ordinal()+1];
		}
	}

	private ReadMode mode = ReadMode.METADATA;

	@Override
	public boolean isBinaryRedstone() {
		return false;
	}

	@Override
	public int getEmission() {
		return this.readBlock(worldObj);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		//this.readBlock(world);
	}

	private int readBlock(World world) {
		int x = this.getFacingX();
		int y = this.getFacingY();
		int z = this.getFacingZ();
		switch(mode) {
			case TILEENTITY:
				TileEntity te = world.getTileEntity(x, y, z);
				return te != null ? 15 : 0;
			case BLOCK:
				return 0;
			case METADATA:
				return world.getBlockMetadata(x, y, z);
		}
		return 0;
	}

	@Override
	public int getTEIndex() {
		return -1;//RedstoneTiles.BLOCKREADER.ordinal();
	}

	public void increment() {
		mode = mode.next();
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.5F);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		mode = ReadMode.list[NBT.getInteger("mode")];
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("mode", mode.ordinal());
	}

	@Override
	public int getTopTexture() {
		return mode.ordinal();
	}
}
