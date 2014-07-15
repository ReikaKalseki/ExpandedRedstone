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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.SyncPacket;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityBUD extends ExpandedRedstoneTileEntity {

	private int IDStored;
	private int metaStored;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		if (this.checkForUpdates(world))
			this.sendPulse(20);
		this.setStates(world);
	}

	private void setStates(World world) {
		int x = this.getFacingX();
		int y = this.getFacingY();
		int z = this.getFacingZ();
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		IDStored = id;
		metaStored = meta;
	}

	private boolean checkForUpdates(World world) {
		int x = this.getFacingX();
		int y = this.getFacingY();
		int z = this.getFacingZ();
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		//ReikaJavaLibrary.pConsole(id+":"+meta+" - "+IDStored+":"+metaStored, Side.SERVER);
		return id != IDStored || meta != metaStored;
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.BUD.ordinal();
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		IDStored = NBT.getInteger("ids");
		metaStored = NBT.getInteger("metas");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("ids", IDStored);
		NBT.setInteger("metas", metaStored);
	}

	@Override
	public boolean canPowerSide(int s) {
		if (this.getFacing() == null)
			return false;
		return s == this.getFacing().ordinal();
	}
}
