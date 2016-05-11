/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.ExpandedRedstone.Base.AnalogWireless;
import Reika.ExpandedRedstone.Base.InventoriedRedstoneTileEntity;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.GUI.ContainerPlacer;
import Reika.ExpandedRedstone.GUI.GuiAnalog;
import Reika.ExpandedRedstone.GUI.GuiPlacer;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiLoader implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileRedstoneBase te = (TileRedstoneBase)world.getTileEntity(x, y, z);
		RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
		if (r.hasInventory()) {
			return new ContainerPlacer(player, (InventoriedRedstoneTileEntity)te);
		}
		if (r == RedstoneTiles.ANALOGTRANSMITTER || r == RedstoneTiles.ANALOGRECEIVER)
			return new CoreContainer(player, te);
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileRedstoneBase te = (TileRedstoneBase)world.getTileEntity(x, y, z);
		RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
		if (r.hasInventory()) {
			return new GuiPlacer(player, (InventoriedRedstoneTileEntity)te);
		}
		if (r == RedstoneTiles.ANALOGTRANSMITTER || r == RedstoneTiles.ANALOGRECEIVER)
			return new GuiAnalog(player, (AnalogWireless)te);
		return null;
	}

}
