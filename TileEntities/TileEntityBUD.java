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

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityBUD extends TileRedstoneBase {

	private Block IDStored;
	private int metaStored;

	private int cooldown = 0;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		if (cooldown > 0) {
			cooldown--;
		}
		else if (this.checkForUpdates(world)) {
			cooldown = 8;
			this.sendPulse(4);
		}
		this.setStates(world);
	}

	private void setStates(World world) {
		int x = this.getFacingX();
		int y = this.getFacingY();
		int z = this.getFacingZ();
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		IDStored = b;
		metaStored = meta;
	}

	private boolean checkForUpdates(World world) {
		int x = this.getFacingX();
		int y = this.getFacingY();
		int z = this.getFacingZ();
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		//ReikaJavaLibrary.pConsole(id+":"+meta+" - "+IDStored+":"+metaStored, Side.SERVER);
		return b != IDStored || meta != metaStored;
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.BUD.ordinal();
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		IDStored = Block.getBlockById(NBT.getInteger("ids"));
		metaStored = NBT.getInteger("metas");
		cooldown = NBT.getInteger("cool");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("ids", Block.getIdFromBlock(IDStored));
		NBT.setInteger("metas", metaStored);
		NBT.setInteger("cool", cooldown);
	}

	@Override
	public boolean canPowerSide(int s) {
		if (this.getFacing() == null)
			return false;
		return s != this.getFacing().getOpposite().ordinal();
	}
}
