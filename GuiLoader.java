/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Base.InventoriedRedstoneTileEntity;
import Reika.ExpandedRedstone.GUI.ContainerRedstone;
import Reika.ExpandedRedstone.GUI.GuiRedstone;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiLoader implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		ExpandedRedstoneTileEntity te = (ExpandedRedstoneTileEntity)world.getBlockTileEntity(x, y, z);
		RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
		if (r.hasInventory()) {
			return new ContainerRedstone(player, (InventoriedRedstoneTileEntity)te);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		ExpandedRedstoneTileEntity te = (ExpandedRedstoneTileEntity)world.getBlockTileEntity(x, y, z);
		RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
		if (r.hasInventory()) {
			return new GuiRedstone(player, (InventoriedRedstoneTileEntity)te);
		}
		return null;
	}

}
