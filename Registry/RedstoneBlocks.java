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
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import Reika.DragonAPI.Interfaces.IDRegistry;
import Reika.DragonAPI.Interfaces.RegistrationList;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.ItemBlocks.BlockRedTile;

public enum RedstoneBlocks implements RegistrationList, IDRegistry {

	TILEENTITY(BlockRedTile.class);

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
		return Material.rock;
	}

	public int getBlockID() {
		return ExpandedRedstone.config.getBlockID(this.ordinal());
	}

	@Override
	public String getUnlocalizedName() {
		return ReikaJavaLibrary.stripSpaces(this.getBasicName());
	}

	@Override
	public Class getObjectClass() {
		return blockClass;
	}

	@Override
	public String getBasicName() {
		return this.name().substring(0, 1)+this.name().substring(1).toLowerCase();
	}

	@Override
	public String getMultiValuedName(int meta) {
		return null;
	}

	@Override
	public boolean hasMultiValuedName() {
		return false;
	}

	@Override
	public int getNumberMetadatas() {
		return 1;
	}

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return null;
	}

	@Override
	public boolean hasItemBlock() {
		return false;
	}

	public boolean isDummiedOut() {
		return blockClass == null;
	}

}
