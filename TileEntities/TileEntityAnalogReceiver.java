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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ExpandedRedstone.Base.AnalogWireless;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityAnalogReceiver extends AnalogWireless {

	private int cachedValue;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);

		int t = this.getTicksExisted();
		if (t == 1 || t == 20 || t == 300)
			this.update();
	}

	@Override
	public void update() {
		this.calcValue(); //before, not after
		super.update();
	}

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

	private void calcValue() {
		cachedValue = placerUUID != null ? this.getChannels()[channel] : 0;
	}

	@Override
	public int getEmission() {
		return cachedValue;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		cachedValue = NBT.getInteger("cache");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("cache", cachedValue);
	}

}
