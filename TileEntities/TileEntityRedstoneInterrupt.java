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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityRedstoneInterrupt extends TileRedstoneBase {

	private States state = States.PASS;

	@Override
	public int getTEIndex() {
		return RedstoneTiles.INTERRUPT.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
	}

	@Override
	public boolean canPowerSide(int s) {
		return s == this.getFacing().getOpposite().ordinal() || s == 1;
	}

	@Override
	public boolean isBinaryRedstone() {
		return false;
	}

	@Override
	public int getEmission() {
		switch(state) {
			case BLOCK:
				return 0;
			case SET:
				return 15;
			case PASS:
			default:
				return this.getPowerInBack();
		}
	}

	public void togglePass() {
		if (state == States.PASS) {
			state = this.getPowerInBack() > 0 ? States.SET : States.BLOCK;
		}
		else {
			state = States.PASS;
		}
		this.update();
		this.syncAllData(true);
	}

	public void toggleState() {
		if (state != States.PASS) {
			state = state == States.BLOCK ? States.SET : States.BLOCK;
			this.update();
			this.syncAllData(true);
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		state = States.values()[NBT.getInteger("state")];
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("state", state.ordinal());
	}

	@Override
	public int getTopTexture() {
		return state.ordinal();
	}

	public static enum States {
		PASS(),
		SET(),
		BLOCK();
	}

}
