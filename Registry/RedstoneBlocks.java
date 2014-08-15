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

import Reika.DragonAPI.Instantiable.Data.PluralMap;
import Reika.DragonAPI.Interfaces.BlockEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.Base.BlockRedstoneBase;
import Reika.ExpandedRedstone.ItemBlocks.BlockExpandedWire;
import Reika.ExpandedRedstone.ItemBlocks.BlockRedstoneCamo;
import Reika.ExpandedRedstone.ItemBlocks.BlockRedstoneMachine;
import Reika.ExpandedRedstone.ItemBlocks.BlockRedstoneTile;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public enum RedstoneBlocks implements BlockEnum {

	THINTILE(BlockRedstoneTile.class),
	WIRE(BlockExpandedWire.class),
	FULLBLOCK(BlockRedstoneMachine.class),
	CAMO(BlockRedstoneCamo.class);

	private final Class blockClass;
	private final int offset;

	public static final RedstoneBlocks[] blockList = RedstoneBlocks.values();

	private static final HashMap<Block, RedstoneBlocks> IDMap = new HashMap();
	private static final PluralMap<RedstoneBlocks> classMap = new PluralMap(2);

	private RedstoneBlocks(Class<? extends Block> cl) {
		this(cl, 0);
	}

	private RedstoneBlocks(Class<? extends Block> cl, int o) {
		blockClass = cl;
		offset = o;
	}

	static RedstoneBlocks getBlockFromClassAndOffset(Class<? extends Block> c, int i) {
		return classMap.get(c, i);
	}

	@Override
	public Class[] getConstructorParamTypes() {
		return new Class[]{Material.class};
	}

	@Override
	public Object[] getConstructorParams() {
		return new Object[]{this.getBlockMaterial()};
	}

	public Material getBlockMaterial() {
		switch(this) {
		case WIRE:
			return Material.circuits;
		default:
			return Material.portal;
		}
	}

	public Block getBlockInstance() {
		return ExpandedRedstone.blocks[this.ordinal()];
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
		return this.isTileEntity();
	}

	private boolean isTileEntity() {
		return BlockRedstoneBase.class.isAssignableFrom(blockClass);
	}

	@Override
	public int getNumberMetadatas() {
		return this.isTileEntity() ? 16 : 1;
	}

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return null;
	}

	@Override
	public boolean hasItemBlock() {
		return this.getItemBlock() != null;
	}

	public boolean isDummiedOut() {
		return blockClass == null;
	}

	public Item getItem() {
		return Item.getItemFromBlock(this.getBlockInstance());
	}

	public static void loadMappings() {
		for (int i = 0; i < blockList.length; i++) {
			RedstoneBlocks block = blockList[i];
			IDMap.put(block.getBlockInstance(), block);
			classMap.put(block, block.blockClass, block.offset);
		}
	}

}