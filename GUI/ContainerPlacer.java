/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.GUI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.ExpandedRedstone.Base.InventoriedRedstoneTileEntity;

public class ContainerPlacer extends CoreContainer
{
	private InventoriedRedstoneTileEntity tile;

	public ContainerPlacer(EntityPlayer ep, InventoriedRedstoneTileEntity te)
	{
		super(ep, te);
		tile = te;
		int i;
		int j;

		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 3; ++j) {
				this.addSlotToContainer(new Slot(te, j + i * 3, 62 + j * 18, 17 + i * 18));
			}
		}

		this.addPlayerInventory(ep);
	}
}
