/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.world.World;

import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;


public class TileEntitySignalTimer extends TileRedstoneBase {

	private long lastEdge;
	private long lastDuration;

	@Override
	public RedstoneTiles getTile() {
		return RedstoneTiles.TIMER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void onPositiveRedstoneEdge() {
		this.addEdge(worldObj);
	}

	private void addEdge(World world) {
		long time = world.getTotalWorldTime();
		lastDuration = time-lastEdge;
		lastEdge = time;
	}

	public String getIntervalMessage() {
		return lastDuration == 0 ? "Not yet triggered" : String.valueOf(this.getInterval());
	}

	public long getInterval() {
		return lastDuration;
	}

}
