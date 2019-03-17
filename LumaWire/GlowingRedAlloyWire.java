/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.LumaWire;

import Reika.ExpandedRedstone.LumaWire.LumaWires.LumaWireEntry;

import mrtjp.projectred.transmission.RedAlloyWirePart;
import mrtjp.projectred.transmission.WireDef.WireDef;


public class GlowingRedAlloyWire extends RedAlloyWirePart {

	private final LumaWireEntry wire;

	public GlowingRedAlloyWire(LumaWireEntry e) {
		wire = e;
	}

	private int getBrightness() {
		int uns = this.signal() & 0xFF;
		return uns/16;
	}

	@Override
	public int getLightValue() {
		return wire.getLightValue(this.getBrightness());
	}

	@Override
	public int renderHue() {
		return wire.getColor(this.getBrightness());//super.renderHue();
	}

	@Override
	public void onSignalUpdate() {
		super.onSignalUpdate();
		this.onBlockUpdate();
	}

	private void onBlockUpdate() {
		this.world().markBlockForUpdate(this.x(), this.y(), this.z());
	}

	@Override
	public WireDef getWireType() {
		return wire.getDefinition();
	}

	@Override
	public String getType() {
		return wire.getID();
	}

}
