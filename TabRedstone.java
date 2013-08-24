/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import Reika.ExpandedRedstone.Registry.RedstoneBlocks;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TabRedstone extends CreativeTabs {

	public TabRedstone(int position, String tabID) {
		super(position, tabID); //The constructor for your tab
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return new ItemStack(RedstoneBlocks.TILEENTITY.getBlockID(), 1, RedstoneTiles.BREAKER.ordinal());
	}

	@Override
	public String getTranslatedTabLabel() {
		return "Expanded Redstone"; //The name of the tab ingame
	}

}
