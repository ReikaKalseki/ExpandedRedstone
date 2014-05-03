/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.Registry;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.TileEntities.TileEntity555;
import Reika.ExpandedRedstone.TileEntities.TileEntityAnalogReceiver;
import Reika.ExpandedRedstone.TileEntities.TileEntityAnalogTransmitter;
import Reika.ExpandedRedstone.TileEntities.TileEntityBUD;
import Reika.ExpandedRedstone.TileEntities.TileEntityBreaker;
import Reika.ExpandedRedstone.TileEntities.TileEntityCamo;
import Reika.ExpandedRedstone.TileEntities.TileEntityChestReader;
import Reika.ExpandedRedstone.TileEntities.TileEntityColumnDecrementer;
import Reika.ExpandedRedstone.TileEntities.TileEntityDriver;
import Reika.ExpandedRedstone.TileEntities.TileEntityEffector;
import Reika.ExpandedRedstone.TileEntities.TileEntityEmitter;
import Reika.ExpandedRedstone.TileEntities.TileEntityEqualizer;
import Reika.ExpandedRedstone.TileEntities.TileEntityHopperTicker;
import Reika.ExpandedRedstone.TileEntities.TileEntityPlacer;
import Reika.ExpandedRedstone.TileEntities.TileEntityProximity;
import Reika.ExpandedRedstone.TileEntities.TileEntityReceiver;
import Reika.ExpandedRedstone.TileEntities.TileEntityRedstonePump;
import Reika.ExpandedRedstone.TileEntities.TileEntityShockPanel;
import Reika.ExpandedRedstone.TileEntities.TileEntitySignalScaler;
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
	RECEIVER("Signal Receiver", TileEntityReceiver.class),
	SHOCK("Shock Panel", TileEntityShockPanel.class),
	PUMP("Redstone Pump", TileEntityRedstonePump.class),
	HOPPER("Hopper Ticker", TileEntityHopperTicker.class),
	SCALER("Signal Scaler", TileEntitySignalScaler.class),
	COLUMN("Column Decrementer", TileEntityColumnDecrementer.class),
	ANALOGTRANSMITTER("Analog Wireless Transmitter", TileEntityAnalogTransmitter.class),
	ANALOGRECEIVER("Analog Wireless Receiver", TileEntityAnalogReceiver.class),
	EQUALIZER("Equalizer", TileEntityEqualizer.class);

	private Class te;
	private String name;

	public static final RedstoneTiles[] TEList = RedstoneTiles.values();

	private RedstoneTiles(String n, Class<? extends ExpandedRedstoneTileEntity> cl) {
		te = cl;
		name = n;
	}

	public RedstoneBlocks getBlockVariable() {
		return this.ordinal() >= 16 ? RedstoneBlocks.TILEENTITY2 : RedstoneBlocks.TILEENTITY;
	}

	public int getBlockID() {
		return this.getBlockVariable().getBlockID();
	}

	public int getBlockMetadata() {
		return this.ordinal()%16;
	}

	public static TileEntity createTEFromIDandMetadata(int id, int meta) {
		int index = getIndexFromIDandMetadata(id, meta);
		Class TEClass = TEList[index].te;
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

	public static RedstoneTiles getTEAt(IBlockAccess world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		if (id != RedstoneBlocks.TILEENTITY.getBlockID() && id != RedstoneBlocks.TILEENTITY2.getBlockID())
			return null;
		int offset = id == RedstoneBlocks.TILEENTITY.getBlockID() ? 0 : 16;
		int meta = world.getBlockMetadata(x, y, z);
		int index = getIndexFromIDandMetadata(id, meta);
		return TEList[index];
	}

	public static int getIndexFromIDandMetadata(int id, int meta) {
		int offset = id == RedstoneBlocks.TILEENTITY.getBlockID() ? 0 : 16;
		return meta+offset;
	}

	public Class<? extends TileEntity> getTEClass() {
		return te;
	}

	public String getName() {
		return name;
	}

	public boolean hasSneakActions() {
		switch(this) {
		case DRIVER:
		case PROXIMITY:
		case SCALER:
		case EQUALIZER:
			return true;
		default:
			return false;
		}
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
		case HOPPER:
		case SCALER:
		case ANALOGTRANSMITTER:
		case ANALOGRECEIVER:
		case EQUALIZER:
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
			return 8;
		case CLOCK:
			return TileEntity555.Settings.list.length;
		case DRIVER:
			return 16;
		case SHOCK:
			return 2;
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
		case SHOCK:
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
		case SHOCK:
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
		return new ItemStack(RedstoneItems.PLACER.getShiftedID(), 1, this.ordinal());
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
		case PROXIMITY:
		case WEATHER:
		case CHESTREADER:
		case DRIVER:
		case SHOCK:
			return true;
		default:
			return false;
		}
	}

	public boolean isDirectionable() {
		switch(this) {
		case PUMP:
			return false;
		default:
			return true;
		}
	}

	public boolean hasHardcodedDirectionTexture(ForgeDirection dir) {
		switch(this) {
		case PUMP:
			return dir == ForgeDirection.DOWN;
		default:
			return false;
		}
	}

	public boolean hasHardcodedDirectionTextures() {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			if (this.hasHardcodedDirectionTexture(dir))
				return true;
		}
		return false;
	}

	public boolean isOpaque() {
		switch(this) {
		case BREAKER:
		case BUD:
		case EFFECTOR:
		case PLACER:
		case EMITTER:
		case RECEIVER:
		case SHOCK:
		case PUMP:
		case COLUMN:
			return true;
		default:
			return false;
		}
	}

}
