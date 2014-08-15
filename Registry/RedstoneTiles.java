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

import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.BlockMap;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.Base.BlockRedstoneBase;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.ItemBlocks.BlockRedstoneCamo;
import Reika.ExpandedRedstone.ItemBlocks.BlockRedstoneMachine;
import Reika.ExpandedRedstone.ItemBlocks.BlockRedstoneTile;
import Reika.ExpandedRedstone.TileEntities.TileEntity555;
import Reika.ExpandedRedstone.TileEntities.TileEntityAnalogReceiver;
import Reika.ExpandedRedstone.TileEntities.TileEntityAnalogTransmitter;
import Reika.ExpandedRedstone.TileEntities.TileEntityBUD;
import Reika.ExpandedRedstone.TileEntities.TileEntityBreaker;
import Reika.ExpandedRedstone.TileEntities.TileEntityCamo;
import Reika.ExpandedRedstone.TileEntities.TileEntityChestReader;
import Reika.ExpandedRedstone.TileEntities.TileEntityColumnDecrementer;
import Reika.ExpandedRedstone.TileEntities.TileEntityCountdown;
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

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;

public enum RedstoneTiles {

	BUD("Block Update Detector", 						TileEntityBUD.class, 				BlockRedstoneMachine.class, 	0),
	BREAKER("Block Breaker", 							TileEntityBreaker.class, 			BlockRedstoneMachine.class, 	1),
	PLACER("Block Placer", 								TileEntityPlacer.class, 			BlockRedstoneMachine.class, 	2),
	EFFECTOR("Item Effector", 							TileEntityEffector.class, 			BlockRedstoneMachine.class, 	3),
	PROXIMITY("Proximity Detector",						TileEntityProximity.class, 			BlockRedstoneTile.class, 		0),
	TOGGLE("Toggle Latch", 								TileEntityToggle.class, 			BlockRedstoneTile.class, 		1),
	WEATHER("Weather Sensor", 							TileEntityWeather.class, 			BlockRedstoneTile.class, 		2),
	CHESTREADER("Chest Reader", 						TileEntityChestReader.class, 		BlockRedstoneTile.class, 		3),
	DRIVER("Signal Driver", 							TileEntityDriver.class, 			BlockRedstoneTile.class, 		4),
	CLOCK("Redstone Clock", 							TileEntity555.class, 				BlockRedstoneTile.class, 		5),
	CAMOFLAGE("Camouflage Block", 						TileEntityCamo.class, 				BlockRedstoneCamo.class, 		0),
	EMITTER("Signal Emitter", 							TileEntityEmitter.class, 			BlockRedstoneMachine.class, 	4),
	RECEIVER("Signal Receiver", 						TileEntityReceiver.class, 			BlockRedstoneMachine.class, 	5),
	SHOCK("Shock Panel", 								TileEntityShockPanel.class, 		BlockRedstoneMachine.class, 	6),
	PUMP("Redstone Pump", 								TileEntityRedstonePump.class, 		BlockRedstoneMachine.class, 	7),
	HOPPER("Hopper Ticker", 							TileEntityHopperTicker.class, 		BlockRedstoneTile.class, 		6),
	SCALER("Signal Scaler", 							TileEntitySignalScaler.class, 		BlockRedstoneTile.class, 		7),
	COLUMN("Column Decrementer", 						TileEntityColumnDecrementer.class, 	BlockRedstoneMachine.class, 	8),
	ANALOGTRANSMITTER("Analog Wireless Transmitter", 	TileEntityAnalogTransmitter.class, 	BlockRedstoneTile.class, 		8),
	ANALOGRECEIVER("Analog Wireless Receiver", 			TileEntityAnalogReceiver.class, 	BlockRedstoneTile.class, 		9),
	EQUALIZER("Equalizer", 								TileEntityEqualizer.class, 			BlockRedstoneTile.class, 		10),
	COUNTDOWN("Countdown Timer", 						TileEntityCountdown.class, 			BlockRedstoneTile.class, 		11);

	private final Class te;
	private final String name;
	private final RedstoneBlocks block;
	private final int meta;

	private static final BlockMap<RedstoneTiles> tileMappings = new BlockMap();

	public static final RedstoneTiles[] TEList = RedstoneTiles.values();

	private RedstoneTiles(String n, Class<? extends TileRedstoneBase> cl, Class<? extends BlockRedstoneBase> b, int m) {
		te = cl;
		name = n;
		block = RedstoneBlocks.getBlockFromClassAndOffset(b, m/16);
		if (block == null) {
			throw new RegistrationException(ExpandedRedstone.instance, "Tile "+this.name()+" registered with a null block!");
		}
		meta = m%16;
	}

	public Block getBlock() {
		return block.getBlockInstance();
	}

	public int getBlockMetadata() {
		return meta;
	}

	public static TileEntity createTEFromIDandMetadata(Block id, int meta) {
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
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return getTEFromIDAndMetadata(id, meta);
	}

	public static RedstoneTiles getTEFromIDAndMetadata(Block id, int meta) {
		return tileMappings.get(id, meta);
	}

	public static int getIndexFromIDandMetadata(Block id, int meta) {
		RedstoneTiles r = tileMappings.get(id, meta);
		return r != null ? r.ordinal() : -1;
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
		case COUNTDOWN:
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
		case COUNTDOWN:
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
		case COUNTDOWN:
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
		case COUNTDOWN:
			return 11;
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
		return new ItemStack(RedstoneItems.PLACER.getItemInstance(), 1, this.ordinal());
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

	public static void loadMappings() {
		for (int i = 0; i < RedstoneTiles.TEList.length; i++) {
			RedstoneTiles r = RedstoneTiles.TEList[i];
			Block id = r.getBlock();
			int meta = r.getBlockMetadata();
			tileMappings.put(id, meta, r);
		}
	}

}