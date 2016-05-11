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

import Reika.ExpandedRedstone.Base.AnalogWireless;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityAnalogReceiver extends AnalogWireless {

	@Override
	public int getTEIndex() {
		return RedstoneTiles.ANALOGRECEIVER.ordinal();
	}

	@Override
	public boolean isBinaryRedstone() {
		return false;
	}

	@Override
	public boolean canPowerSide(int s) {
		return s == this.getFacing().getOpposite().ordinal();
	}

	@Override
	public int getEmission() {
		return placerUUID != null ? this.getChannels()[channel] : 0;
	}

}
