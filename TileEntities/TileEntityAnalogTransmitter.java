/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import net.minecraft.world.World;
import Reika.ExpandedRedstone.Base.AnalogWireless;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityAnalogTransmitter extends AnalogWireless {

	private int ticksToCalc = 2;

	@Override
	public int getTEIndex() {
		return RedstoneTiles.ANALOGTRANSMITTER.ordinal();
	}

	@Override
	public void setChannel(int channel) {
		super.setChannel(channel);
		this.recalculate();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (ticksToCalc > -1)
			ticksToCalc--;
		if (ticksToCalc == 0)
			this.recalculate();
	}

	private void recalculate() {
		int prev = channels[channel];
		this.recalculateChannel(channel);
		if (channels[channel] != prev) {
			this.updateReceivers();
		}
	}

	public void markRecalculationIn(int ticks) {
		ticksToCalc = ticks;
	}

	@Override
	public void remove() {
		super.remove();
		this.recalculate();
	}

	private void updateReceivers() {
		try {
			ArrayList<TileEntityAnalogReceiver> li = receivers[channel];
			if (receivers[channel] == null || receivers[channel].isEmpty())
				return;
			for (int i = 0; i < li.size(); i++) {
				TileEntityAnalogReceiver te = li.get(i);
				te.update();
				//ReikaJavaLibrary.pConsole(this+" >> "+te, Side.SERVER);
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace(); //only happens in compiled MC...!?
		}
	}

	private static void recalculateChannel(int channel) {
		ArrayList<TileEntityAnalogTransmitter> li = transmitters[channel];
		if (li == null || li.isEmpty()) {
			channels[channel] = 0;
			return;
		}
		int max = 0;
		for (int i = 0; i < li.size(); i++) {
			TileEntityAnalogTransmitter te = li.get(i);
			int lvl = te.worldObj.getBlockPowerInput(te.xCoord, te.yCoord, te.zCoord);
			if (lvl > max)
				max = lvl;
		}
		channels[channel] = max;
	}

}
