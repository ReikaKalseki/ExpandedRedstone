/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import net.minecraft.world.World;
import Reika.DragonAPI.Auxiliary.ModularLogger;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.ExpandedRedstone.ExpandedRedstone;
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

	@Override
	public void update() {
		super.update();
		this.recalculate();
	}

	private void recalculate() {
		if (placerUUID != null) {
			int[] ch = this.getChannels();
			int prev = ch[channel];
			this.recalculateChannel(channel);
			ch = this.getChannels();
			if (ExpandedRedstone.logger.shouldDebug()) {
				ExpandedRedstone.logger.log("Recalculated wireless redstone channel "+channel+" for "+placerUUID+": "+prev+">"+ch[channel]);
			}
			if (ch[channel] != prev) {
				this.updateReceivers();
			}
			ModularLogger.instance.log(LOGGER_ID, "Wireless user "+placerUUID+" channel "+channel+" recalculated");
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
			ArrayList<WorldLocation> li = receivers[channel];
			if (receivers[channel] == null || receivers[channel].isEmpty())
				return;
			for (WorldLocation loc : li) {
				TileEntityAnalogReceiver te = (TileEntityAnalogReceiver)loc.getTileEntity();
				te.update();
				ModularLogger.instance.log(LOGGER_ID, "Wireless receiver "+te+" on channel "+channel+" updated");
				//ReikaJavaLibrary.pConsole(this+" >> "+te, Side.SERVER);
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace(); //only happens in compiled MC...!?
		}
	}

	private void recalculateChannel(int channel) {
		ArrayList<WorldLocation> li = transmitters[channel];
		if (li == null || li.isEmpty()) {
			this.getChannels()[channel] = 0;
			return;
		}
		int max = 0;
		for (WorldLocation loc : li) {
			TileEntityAnalogTransmitter te = (TileEntityAnalogTransmitter)loc.getTileEntity();
			int lvl = this.getRedstoneLevelTo(this.getFacing().getOpposite());
			if (lvl > max)
				max = lvl;
		}
		this.getChannels()[channel] = max;
		ModularLogger.instance.log(LOGGER_ID, "Wireless channel "+channel+" on "+placerUUID+" recalculated to "+max);
	}

}
