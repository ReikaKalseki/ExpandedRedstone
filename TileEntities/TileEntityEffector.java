/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;
import Reika.ExpandedRedstone.Base.InventoriedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityEffector extends InventoriedRedstoneTileEntity {

	private boolean lastPower;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		if (ReikaRedstoneHelper.isPositiveEdge(world, x, y, z, lastPower)) {
			this.useItem();
		}
		lastPower = world.isBlockIndirectlyGettingPowered(x, y, z);
	}

	private void useItem() {
		for (int i = 0; i < inv.length; i++) {
			ItemStack is = inv[i];
			if (is != null) {
				this.fakeClick(i, is);
				return;
			}
		}
	}

	private void fakeClick(int slot, ItemStack is) {
		World world = worldObj;
		int dx = this.getFacingX();
		int dy = this.getFacingY();
		int dz = this.getFacingZ();
		EntityPlayer ep = this.getPlacer();
		Item it = is.getItem();
		it.onItemUse(is, ep, world, dx, dy, dz, this.getFacing().getOpposite().ordinal(), 0F, 0F, 0F);
		inv[slot] = is;
		if (RedstoneOptions.NOISES.getState())
			ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click");
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.EFFECTOR.ordinal();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
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
