/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.ExpandedRedstone.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntity555 extends ExpandedRedstoneTileEntity {

	private StepTimer timer;

	private int onTime;
	private int offTime;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		if (timer == null)
			timer = new StepTimer(20);
		timer.update();
		if (timer.checkCap())
			emit = !emit;
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.CLOCK.ordinal();
	}

	public void setLoTime(int time) {
		offTime = time;
	}

	public void setHiTime(int time) {
		onTime = time;
	}
}
