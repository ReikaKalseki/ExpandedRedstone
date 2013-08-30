/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.Registry;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Libraries.ReikaItemHelper;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.TileEntities.TileEntity555;
import Reika.ExpandedRedstone.TileEntities.TileEntityBUD;
import Reika.ExpandedRedstone.TileEntities.TileEntityBreaker;
import Reika.ExpandedRedstone.TileEntities.TileEntityCamo;
import Reika.ExpandedRedstone.TileEntities.TileEntityChestReader;
import Reika.ExpandedRedstone.TileEntities.TileEntityDriver;
import Reika.ExpandedRedstone.TileEntities.TileEntityEffector;
import Reika.ExpandedRedstone.TileEntities.TileEntityEmitter;
import Reika.ExpandedRedstone.TileEntities.TileEntityPlacer;
import Reika.ExpandedRedstone.TileEntities.TileEntityProximity;
import Reika.ExpandedRedstone.TileEntities.TileEntityReceiver;
import Reika.ExpandedRedstone.TileEntities.TileEntityToggle;
import Reika.ExpandedRedstone.TileEntities.TileEntityWeather;
import cpw.mods.fml.common.registry.GameRegistry;

public enum RedstoneTiles {

	BUD("Block Update Detector", TileEntityBUD.class),
	BREAKER("Block Breaker", TileEntityBreaker.class),
	PLACER("Block Placer", TileEntityPlacer.class),
	EFFECTOR("Item Effector", TileEntityEffector.class),
	PROXIMITY("Proximity Detector", TileEntityProximity.class),
	TOGGLE("Toggle Latch", TileEntityToggle.class),
	WEATHER("Weather Sensor", TileEntityWeather.class),
	CHESTREADER("Chest Reader", TileEntityChestReader.class),
	DRIVER("Signal Driver", TileEntityDriver.class),
	CLOCK("Redstone Clock", TileEntity555.class),
	CAMOFLAGE("Camouflage Block", TileEntityCamo.class),
	EMITTER("Signal Emitter", TileEntityEmitter.class),
	RECEIVER("Signal Receiver", TileEntityReceiver.class);

	private Class te;
	private String name;

	public static final RedstoneTiles[] TEList = RedstoneTiles.values();

	private RedstoneTiles(String n, Class<? extends TileEntity> cl) {
		te = cl;
		name = n;
	}

	public static TileEntity createTEFromMetadata(int meta) {
		Class TEClass = TEList[meta].te;
		try {
			return (TileEntity)TEClass.newInstance();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new RegistrationException(ExpandedRedstone.instance, "Metadata "+meta+" failed to instantiate its TileEntity of "+TEClass);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RegistrationException(ExpandedRedstone.instance, "Metadata "+meta+" failed illegally accessed its TileEntity of "+TEClass);
		}
	}

	public static RedstoneTiles getTEAt(World world, int x, int y, int z) {
		if (world.getBlockId(x, y, z) != RedstoneBlocks.TILEENTITY.getBlockID())
			return null;
		int meta = world.getBlockMetadata(x, y, z);
		if (meta >= TEList.length)
			return null;
		return TEList[meta];
	}

	public Class<? extends TileEntity> getTEClass() {
		return te;
	}

	public String getName() {
		return name;
	}

	public boolean hasSneakActions() {
		if (this == DRIVER)
			return true;
		return false;
	}

	public boolean hasInventory() {
		return IInventory.class.isAssignableFrom(te);
	}

	public boolean isThinTile() {
		switch(this) {
		case TOGGLE:
		case CHESTREADER:
		case WEATHER:
		case CLOCK:
		case DRIVER:
		case PROXIMITY:
			return true;
		default:
			return false;
		}
	}

	public boolean hasVariableTopTexture() {
		switch(this) {
		case CHESTREADER:
		case CLOCK:
		case DRIVER:
		case TOGGLE:
		case PROXIMITY:
			return true;
		default:
			return false;
		}
	}

	public int getTextureStates() {
		switch(this) {
		case BREAKER:
			return 8;
		case PLACER:
		case EFFECTOR:
		case RECEIVER:
		case EMITTER:
			return 2;
		case CHESTREADER:
			return 2;
		case TOGGLE:
			return 4;
		case PROXIMITY:
			return 4;
		case CLOCK:
			return TileEntity555.Settings.list.length;
		case DRIVER:
			return 16;
		default:
			return 1;
		}
	}

	public boolean canBeVertical() {
		switch(this) {
		case PLACER:
		case BREAKER:
		case EFFECTOR:
		case BUD:
		case EMITTER:
		case RECEIVER:
			return true;
		default:
			return false;
		}
	}

	public boolean isVariableTexture() {
		switch(this) {
		case BREAKER:
		case PLACER:
		case EFFECTOR:
		case RECEIVER:
		case EMITTER:
			return true;
		default:
			return false;
		}
	}

	public boolean isReversedTopTexture() {
		switch(this) {
		case TOGGLE:
		case CLOCK:
		case DRIVER:
			return true;
		default:
			return false;
		}
	}

	public boolean isOmniTexture() {
		switch(this) {
		case CAMOFLAGE:
			return true;
		default:
			return false;
		}
	}

	public ItemStack getItem() {
		return new ItemStack(Block.blocksList[RedstoneBlocks.TILEENTITY.getBlockID()], 1, this.ordinal());
	}

	public void addRecipe(Object... params) {
		GameRegistry.addRecipe(this.getItem(), params);
	}

	public void addSizedRecipe(int size, Object... params) {
		GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(this.getItem(), size), params);
	}

	public void addRecipe(IRecipe ir) {
		GameRegistry.addRecipe(ir);
	}

	public void addNBTRecipe(int nbt, Object... params) {
		ItemStack is = this.getItem();
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("nbt", nbt);
		if (this == BREAKER)
			is.stackTagCompound.setInteger("dmg", TileEntityBreaker.WOOD_USES);
		GameRegistry.addRecipe(is, params);
	}

	public void addShapelessRecipe(Object... params) {
		GameRegistry.addShapelessRecipe(this.getItem(), params);
	}

	public boolean isReversedPlacement() {
		switch(this) {
		case BREAKER:
		case BUD:
		case EFFECTOR:
		case PLACER:
		case EMITTER:
		case RECEIVER:
			return true;
		default:
			return false;
		}
	}

}
