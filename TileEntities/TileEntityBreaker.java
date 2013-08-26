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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaItemHelper;
import Reika.DragonAPI.Libraries.ReikaRedstoneHelper;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityBreaker extends ExpandedRedstoneTileEntity {

	private boolean lastPower;

	private int harvest;

	@Override
	public int getTEIndex() {
		return RedstoneTiles.BREAKER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		if (ReikaRedstoneHelper.isPositiveEdge(world, x, y, z, lastPower)) {
			this.breakBlock(world);
		}
		lastPower = world.isBlockIndirectlyGettingPowered(x, y, z);
	}

	public boolean canHarvest(int id, int meta) {
		if (id == 0)
			return false;
		if (id == Block.bedrock.blockID)
			return false;
		Block b = Block.blocksList[id];
		if (b.blockHardness < 0)
			return false;
		switch(harvest) {
		case 0:
			return b.blockMaterial.isToolNotRequired();
		case 1:
			return Item.pickaxeWood.canHarvestBlock(b);
		case 2:
			return Item.pickaxeStone.canHarvestBlock(b);
		case 3:
			return true;
		}
		return true;
	}

	private void breakBlock(World world) {
		int dx = this.getFacingX();
		int dy = this.getFacingY();
		int dz = this.getFacingZ();
		int id = world.getBlockId(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		if (!this.canHarvest(id, meta))
			return;
		Block b = Block.blocksList[id];
		ArrayList<ItemStack> items = b.getBlockDropped(world, dx, dy, dz, meta, 0);
		for (int i = 0; i < items.size(); i++) {
			ItemStack is = items.get(i);
			if (!this.chestCheck(world, dx, dy, dz, is))
				ReikaItemHelper.dropItem(world, this.getBackX()+0.5, this.getBackY()+0.5, this.getBackZ()+0.5, is);
		}
		world.setBlock(dx, dy, dz, 0);
	}

	private boolean chestCheck(World world, int x, int y, int z, ItemStack is) {
		if (is == null)
			return false;
		if (world.isRemote)
			return false;
		int dx = this.getBackX();
		int dy = this.getBackY();
		int dz = this.getBackZ();
		TileEntity te = world.getBlockTileEntity(dx, dy, dz);
		IInventory ii;
		if (te instanceof IInventory) {
			ii = (IInventory)te;
			if (ReikaInventoryHelper.addToIInv(is, ii))
				return true;
		}
		return false;
	}

	@Override
	public int getFrontTexture() {
		return harvest*2+(worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) ? 1 : 0);
	}

	public void setHarvestLevel(int level) {
		harvest = level;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		harvest = NBT.getInteger("level");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("level", harvest);
	}

	public int getHarvestLevel() {
		return harvest;
	}

}
