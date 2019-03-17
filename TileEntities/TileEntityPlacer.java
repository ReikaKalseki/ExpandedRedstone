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

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ExpandedRedstone.Base.InventoriedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityPlacer extends InventoriedRedstoneTileEntity {

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
	}

	@Override
	protected void onPositiveRedstoneEdge() {
		if (!worldObj.isRemote && this.canPlace(worldObj)) {
			this.placeBlock(worldObj);
		}
	}

	private boolean canPlace(World world) {
		int dx = this.getFacingX();
		int dy = this.getFacingY();
		int dz = this.getFacingZ();
		return ReikaWorldHelper.softBlocks(world, dx, dy, dz);
	}

	private void placeBlock(World world) {
		int dx = this.getFacingX();
		int dy = this.getFacingY();
		int dz = this.getFacingZ();
		for (int i = 0; i < inv.length; i++) {
			ItemStack is = inv[i];
			if (is != null) {
				BlockKey id = ReikaItemHelper.getWorldBlockFromItem(is);
				if (id.blockID != Blocks.air) {
					if (world.setBlock(dx, dy, dz, id.blockID, id.metadata, 3)) {
						id.blockID.onBlockPlacedBy(world, dx, dy, dz, this.getPlacer(), is);
						ReikaInventoryHelper.decrStack(i, inv);
						ReikaSoundHelper.playPlaceSound(world, dx, dy, dz, id.blockID);
					}
					return;
				}
			}
		}
		if (RedstoneOptions.NOISES.getState())
			ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click");
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.PLACER.ordinal();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		BlockKey id = ReikaItemHelper.getWorldBlockFromItem(itemstack);
		return id.blockID != Blocks.air;
	}

	@Override
	public int getFrontTexture() {
		return this.hasRedstoneSignal() ? 1 : 0;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 9;
	}
}
