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
import Reika.DragonAPI.Libraries.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.ReikaStringParser;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityBreaker extends ExpandedRedstoneTileEntity {

	private boolean lastPower;

	public static final int WOOD_USES = 128;
	public static final int MAX_RANGE = 12;

	public static final ItemStack BARRIER_BLOCK = new ItemStack(Block.blockLapis);

	private Materials harvest;
	private int dura;

	public enum Materials {
		WOOD(),
		STONE(),
		IRON(),
		DIAMOND();

		public static final Materials[] mats = values();

		public String getName() {
			return ReikaStringParser.capFirstChar(this.name());
		}

		public int getDigDistance() {
			return this == DIAMOND ? MAX_RANGE : 1;
		}

		public boolean isDamageable() {
			return this == WOOD;
		}

		public boolean canHarvest(int dura, int id, int meta) {
			if (id == 0)
				return false;
			if (id == Block.bedrock.blockID)
				return false;
			Block b = Block.blocksList[id];
			if (b.blockHardness < 0)
				return false;
			switch(this) {
			case WOOD:
				return dura > 0 && b.blockMaterial.isToolNotRequired();
			case STONE:
				return b.blockMaterial.isToolNotRequired();
			case IRON:
				return Item.pickaxeIron.canHarvestBlock(b) || b.blockMaterial.isToolNotRequired();
			case DIAMOND:
				return id != BARRIER_BLOCK.itemID || meta != BARRIER_BLOCK.getItemDamage();
			default:
				return false;
			}
		}
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.BREAKER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		if (ReikaRedstoneHelper.isPositiveEdge(world, x, y, z, lastPower)) {
			this.breakBlocks(world);
		}
		lastPower = world.isBlockIndirectlyGettingPowered(x, y, z);
	}

	private void breakBlocks(World world) {
		world.playSoundEffect(xCoord+0.5, yCoord+0.5, zCoord+0.5, "random.click", 0.4F, 1F);
		for (int k = 1; k <= harvest.getDigDistance(); k++) {
			int dx = this.getFacingXScaled(k);
			int dy = this.getFacingYScaled(k);
			int dz = this.getFacingZScaled(k);
			int id = world.getBlockId(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (harvest.canHarvest(dura, id, meta)) {
				Block b = Block.blocksList[id];
				ArrayList<ItemStack> items = b.getBlockDropped(world, dx, dy, dz, meta, 0);
				for (int i = 0; i < items.size(); i++) {
					ItemStack is = items.get(i);
					if (!this.chestCheck(world, this.getBackX(), this.getBackY(), this.getBackZ(), is))
						ReikaItemHelper.dropItem(world, this.getBackX()+0.5, this.getBackY()+0.5, this.getBackZ()+0.5, is);
				}
				world.setBlock(dx, dy, dz, 0);
				ReikaSoundHelper.playBreakSound(world, dx, dy, dz, b);
				if (harvest.isDamageable())
					dura--;
			}
		}
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
		return harvest.ordinal()*2+(worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) ? 1 : 0);
	}

	public void setHarvestLevel(int level) {
		harvest = Materials.mats[level];
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		harvest = Materials.mats[NBT.getInteger("level")];
		dura = NBT.getInteger("dmg");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("level", harvest.ordinal());
		NBT.setInteger("dmg", dura);
	}

	public int getHarvestLevel() {
		return harvest.ordinal();
	}

	public int getDurability() {
		return dura;
	}

	public void setDurability(int dura2) {
		dura = dura2;
	}
}
