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

import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ExpandedRedstone.Base.InventoriedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TileEntityPlacer extends InventoriedRedstoneTileEntity {

	private boolean lastPower;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		if (ReikaRedstoneHelper.isPositiveEdge(world, x, y, z, lastPower) && this.canPlace(world)) {
			this.placeBlock(world);
		}
		lastPower = world.isBlockIndirectlyGettingPowered(x, y, z);
	}

	private boolean canPlace(World world) {
		int dx = this.getFacingX();
		int dy = this.getFacingY();
		int dz = this.getFacingZ();
		return (ReikaWorldHelper.softBlocks(world, dx, dy, dz));
	}

	private void placeBlock(World world) {
		int dx = this.getFacingX();
		int dy = this.getFacingY();
		int dz = this.getFacingZ();
		for (int i = 0; i < inv.length; i++) {
			ItemStack is = inv[i];
			if (is != null) {
				Block id = ReikaItemHelper.getWorldBlockIDFromItem(is);
				int meta = ReikaItemHelper.getWorldBlockMetaFromItem(is);
				if (id != Blocks.air) {
					if (world.setBlock(dx, dy, dz, id, meta, 3)) {
						ReikaInventoryHelper.decrStack(i, inv);
						ReikaSoundHelper.playPlaceSound(world, dx, dy, dz, id);
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
		Block id = ReikaItemHelper.getWorldBlockIDFromItem(itemstack);
		return id != Blocks.air;
	}

	@Override
	public int getFrontTexture() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) ? 1 : 0;
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