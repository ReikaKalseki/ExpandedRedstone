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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import Reika.DragonAPI.Interfaces.RegistryEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.ItemBlocks.BlockExpandedWire;
import Reika.ExpandedRedstone.ItemBlocks.BlockRedTile;
import Reika.ExpandedRedstone.ItemBlocks.ItemCircuitPlacer;

public enum RedstoneBlocks implements RegistryEnum {

	TILEENTITY(BlockRedTile.class),
	WIRE(BlockExpandedWire.class);

	private Class blockClass;

	public static final RedstoneBlocks[] blockList = RedstoneBlocks.values();

	private RedstoneBlocks(Class<? extends Block> cl) {
		blockClass = cl;
	}

	@Override
	public String getConfigName() {
		return this.getBasicName();
	}

	@Override
	public int getDefaultID() {
		return 3000+this.ordinal();
	}

	@Override
	public boolean isBlock() {
		return true;
	}

	@Override
	public boolean isItem() {
		return false;
	}

	@Override
	public String getCategory() {
		return "Block IDs";
	}

	@Override
	public Class[] getConstructorParamTypes() {
		return new Class[]{int.class, Material.class};
	}

	@Override
	public Object[] getConstructorParams() {
		return new Object[]{this.getBlockID(), this.getBlockMaterial()};
	}

	public Material getBlockMaterial() {
		switch(this) {
		case WIRE:
			return Material.circuits;
		default:
			return Material.portal;
		}
	}

	public int getBlockID() {
		return ExpandedRedstone.config.getBlockID(this.ordinal());
	}

	@Override
	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(this.getBasicName());
	}

	@Override
	public Class getObjectClass() {
		return blockClass;
	}

	@Override
	public String getBasicName() {
		switch(this) {
		case WIRE:
			return "Lapis Wire";
		default:
			return ReikaStringParser.capFirstChar(this.name());
		}
	}

	@Override
	public String getMultiValuedName(int meta) {
		return RedstoneTiles.TEList[meta].getName();
	}

	@Override
	public boolean hasMultiValuedName() {
		return this == TILEENTITY;
	}

	@Override
	public int getNumberMetadatas() {
		return RedstoneTiles.TEList.length;
	}

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		switch(this) {
		case TILEENTITY:
			return ItemCircuitPlacer.class;
		default:
			return null;
		}
	}

	@Override
	public boolean hasItemBlock() {
		return this.getItemBlock() != null;
	}

	public boolean isDummiedOut() {
		return blockClass == null;
	}

	public int getID() {
		return this.getBlockID();
	}

	@Override
	public boolean overwritingItem() {
		return false;
	}

}
