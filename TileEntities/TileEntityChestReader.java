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

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityChestReader extends ExpandedRedstoneTileEntity {

	private boolean signalFull = true;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		this.readChest(world);
	}

	private void readChest(World world) {
		TileEntity te = world.getBlockTileEntity(this.getFacingX(), this.getFacingY(), this.getFacingZ());
		if (te instanceof IInventory) {
			if (signalFull)
				this.setEmitting(ReikaInventoryHelper.isFull((IInventory)te));
			else
				this.setEmitting(ReikaInventoryHelper.isEmpty((IInventory)te));
		}
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.CHESTREADER.ordinal();
	}

	public void alternate() {
		signalFull = !signalFull;
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.5F);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		signalFull = NBT.getBoolean("full");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setBoolean("full", signalFull);
	}

	@Override
	public int getTopTexture() {
		return signalFull ? 0 : 1;
	}
}
