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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityBreaker extends TileRedstoneBase {

	public static final int WOOD_USES = 128;
	public static final int MAX_RANGE = 12;

	public static final BlockKey BARRIER_BLOCK = new BlockKey(Blocks.lapis_block);

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

		public boolean canHarvest(int dura, Block b, int meta, World world, int x, int y, int z) {
			if (b == Blocks.air)
				return false;
			if (b == Blocks.bedrock)
				return false;
			if (b.getBlockHardness(world, x, y, z) < 0)
				return false;
			if (b instanceof SemiUnbreakable) {
				if (((SemiUnbreakable)b).isUnbreakable(world, x, y, z, meta))
					return false;
			}
			switch(this) {
				case WOOD:
					return dura > 0 && b.getMaterial().isToolNotRequired();
				case STONE:
					Material mat = b.getMaterial();
					return mat.isToolNotRequired() || mat == Material.snow;
				case IRON:
					return Items.iron_pickaxe.canHarvestBlock(b, new ItemStack(Items.iron_pickaxe)) || b.getMaterial().isToolNotRequired();
				case DIAMOND:
					return !BARRIER_BLOCK.match(b, meta);
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
	}

	@Override
	protected void onPositiveRedstoneEdge() {
		if (harvest != null)
			this.breakBlocks(worldObj);
	}

	private void breakBlocks(World world) {
		if (RedstoneOptions.NOISES.getState())
			world.playSoundEffect(xCoord+0.5, yCoord+0.5, zCoord+0.5, "random.click", 0.4F, 1F);
		if (world.isRemote)
			return;
		for (int k = 1; k <= harvest.getDigDistance(); k++) {
			int dx = this.getFacingXScaled(k);
			int dy = this.getFacingYScaled(k);
			int dz = this.getFacingZScaled(k);
			Block b = world.getBlock(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			EntityPlayer ep = this.getPlacer();
			if (harvest.canHarvest(dura, b, meta, world, dx, dy, dz)) {
				ArrayList<ItemStack> items = this.getDrops(world, dx, dy, dz, b, meta);
				MinecraftForge.EVENT_BUS.post(new HarvestDropsEvent(dx, dy, dz, world, b, meta, 0, 1, items, ep, false));
				for (int i = 0; i < items.size(); i++) {
					ItemStack is = items.get(i);
					if (!this.chestCheck(world, this.getBackX(), this.getBackY(), this.getBackZ(), is))
						ReikaItemHelper.dropItem(world, this.getBackX()+0.5, this.getBackY()+0.5, this.getBackZ()+0.5, is);
				}
				world.setBlockToAir(dx, dy, dz);
				ReikaSoundHelper.playBreakSound(world, dx, dy, dz, b);
				if (harvest.isDamageable())
					dura--;
			}
			else if (BARRIER_BLOCK.match(b, meta))
				return;
		}
	}

	private ArrayList<ItemStack> getDrops(World world, int x, int y, int z, Block b, int meta) {
		return b.getDrops(world, x, y, z, meta, 0);
	}

	private boolean chestCheck(World world, int x, int y, int z, ItemStack is) {
		if (is == null)
			return false;
		if (world.isRemote)
			return false;
		int dx = this.getBackX();
		int dy = this.getBackY();
		int dz = this.getBackZ();
		TileEntity te = world.getTileEntity(dx, dy, dz);
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
		if (harvest == null)
			return this.hasRedstoneSignal() ? 1 : 0;
		return harvest.ordinal()*2+(this.hasRedstoneSignal() ? 1 : 0);
	}

	public void setHarvestLevel(int level) {
		harvest = Materials.mats[level];
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		harvest = Materials.mats[NBT.getInteger("level")];
		dura = NBT.getInteger("dmg");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		if (harvest != null)
			NBT.setInteger("level", harvest.ordinal());
		NBT.setInteger("dmg", dura);
	}

	public int getHarvestLevel() {
		return harvest != null ? harvest.ordinal() : 0;
	}

	public int getDurability() {
		return dura;
	}

	public void setDurability(int dura2) {
		dura = dura2;
	}
}
