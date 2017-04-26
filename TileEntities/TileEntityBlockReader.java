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

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import dan200.computercraft.api.lua.LuaException;

public class TileEntityBlockReader extends TileRedstoneBase {

	public static enum ReadMode {
		BLOCK(),
		METADATA(),
		TILEENTITY(),
		LIGHTVAL(),
		CANSEESKY(),
		BIOME();

		public static ReadMode[] list = values();

		private ReadMode() {
			new ReaderLuaMethod(this);
		}

		public ReadMode next() {
			return this.ordinal() == list.length-1 ? list[0] : list[this.ordinal()+1];
		}
	}

	private static class ReaderLuaMethod extends LuaMethod {

		private final ReadMode mode;

		private ReaderLuaMethod(ReadMode mode) {
			super(mode.name().toLowerCase(Locale.ENGLISH), TileEntityBlockReader.class);
			this.mode = mode;
		}

		private Object[] invoke(World world, int x, int y, int z) {
			switch(mode) {
				case BIOME: {
					BiomeGenBase b = world.getBiomeGenForCoords(x, z);
					return new Object[]{b.biomeID, b.biomeName};
				}
				case BLOCK: {
					Block b = world.getBlock(x, y, z);
					return new Object[]{Block.getIdFromBlock(b), b.getUnlocalizedName(), b.getLocalizedName()};
				}
				case CANSEESKY:
					return new Object[]{world.canBlockSeeTheSky(x, y, z)};
				case LIGHTVAL:
					return new Object[]{world.getBlockLightValue(x, y, z)};
				case METADATA:
					return new Object[]{world.getBlockMetadata(x, y, z)};
				case TILEENTITY: {
					TileEntity te = world.getTileEntity(x, y, z);
					return new Object[]{te != null, te != null ? te.getClass().getName() : "null"};
				}
			}
			return null;
		}

		@Override
		public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
			TileEntityBlockReader tile = (TileEntityBlockReader)te;
			return this.invoke(te.worldObj, tile.getFacingX(), tile.getFacingY(), tile.getFacingZ());
		}

		@Override
		public String getDocumentation() {
			return "Gets a world value at the given location.";
		}

		@Override
		public String getArgsAsString() {
			return "";
		}

		@Override
		public ReturnType getReturnType() {
			switch(mode) {
				case CANSEESKY:
					return ReturnType.BOOLEAN;
				case LIGHTVAL:
				case METADATA:
					return ReturnType.INTEGER;
				case BIOME:
				case BLOCK:
				case TILEENTITY:
					return ReturnType.ARRAY;
			}
			return null;
		}
	}

	private ReadMode mode = ReadMode.METADATA;

	@Override
	public int getFrontTexture() {
		return mode.ordinal();
	}

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
			case CANSEESKY:
				return world.canBlockSeeTheSky(x, y, z) ? 15 : 0;
			case LIGHTVAL:
				return world.getBlockLightValue(x, y, z);
			case BIOME:
				return 0;
		}
		return 0;
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.BLOCKREADER.ordinal();
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
