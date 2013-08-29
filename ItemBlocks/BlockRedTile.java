/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.ItemBlocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaItemHelper;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import Reika.ExpandedRedstone.TileEntities.TileEntity555;
import Reika.ExpandedRedstone.TileEntities.TileEntityBreaker;
import Reika.ExpandedRedstone.TileEntities.TileEntityCamo;
import Reika.ExpandedRedstone.TileEntities.TileEntityChestReader;
import Reika.ExpandedRedstone.TileEntities.TileEntityDriver;
import Reika.ExpandedRedstone.TileEntities.TileEntityProximity;
import buildcraft.api.tools.IToolWrench;

public class BlockRedTile extends Block {

	public static Icon trans;
	private Icon[][][] icons = new Icon[6][RedstoneTiles.TEList.length][16];
	private Icon[][] front = new Icon[RedstoneTiles.TEList.length][16];
	private static final String BLANK_TEX = "ExpandedRedstone:basic";
	private static final String BLANK_TEX_2 = "ExpandedRedstone:basic_side";

	public BlockRedTile(int ID, Material mat) {
		super(ID, mat);
		this.setCreativeTab(ExpandedRedstone.tab);
		this.setHardness(0.75F);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return RedstoneTiles.createTEFromMetadata(meta);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();
		RedstoneTiles r = RedstoneTiles.TEList[meta];
		ItemStack is = r.getItem();
		if (r == RedstoneTiles.BREAKER) {
			TileEntityBreaker te = (TileEntityBreaker)world.getBlockTileEntity(x, y, z);
			if (te != null) {
				is.stackTagCompound = new NBTTagCompound();
				is.stackTagCompound.setInteger("nbt", te.getHarvestLevel());
				is.stackTagCompound.setInteger("dmg", te.getDurability());
			}
		}
		li.add(is);
		return li;
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
	{
		if (!player.capabilities.isCreativeMode)
			this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
		return world.setBlock(x, y, z, 0);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		RedstoneTiles r = RedstoneTiles.TEList[meta];
		if (r == RedstoneTiles.BREAKER) {
			TileEntityBreaker brk = (TileEntityBreaker)world.getBlockTileEntity(x, y, z);
			if (brk != null) {
				ItemStack todrop = r.getItem();
				todrop.stackTagCompound = new NBTTagCompound();
				todrop.stackTagCompound.setInteger("nbt", brk.getHarvestLevel());
				todrop.stackTagCompound.setInteger("dmg", brk.getDurability());
				ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, todrop);
			}
		}
		else {
			ItemStack todrop = r.getItem();
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, todrop);
		}
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess iba, int x, int y, int z, int s)
	{
		ExpandedRedstoneTileEntity te = (ExpandedRedstoneTileEntity)iba.getBlockTileEntity(x, y, z);
		if (te.canPowerSide(s)) {
			if (te.isBinaryRedstone())
				return te.isEmitting() ? 15 : 0;
			else
				return te.getEmission();
		}
		else return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess iba, int x, int y, int z, int s) {
		return this.isProvidingWeakPower(iba, x, y, z, s);
	}

	@Override
	public final boolean canBeReplacedByLeaves(World world, int x, int y, int z)
	{
		return false;
	}

	@Override
	public final boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata)
	{
		return false;
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face)
	{
		return 0;
	}

	@Override
	public boolean canDragonDestroy(World world, int x, int y, int z)
	{
		return true;
	}

	@Override
	public final boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public final boolean canProvidePower()
	{
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int par6, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
		if (ep.isSneaking() && !r.hasSneakActions())
			return false;
		ExpandedRedstoneTileEntity te = (ExpandedRedstoneTileEntity)world.getBlockTileEntity(x, y, z);
		if (te == null)
			return false;
		if (is != null && is.getItem() instanceof IToolWrench) {
			te.rotate();
			return true;
		}
		switch (r) {
		case CHESTREADER:
			((TileEntityChestReader)te).alternate();
			return true;
		case CLOCK:
			((TileEntity555)te).incrementSetting();
			return true;
		case DRIVER:
			if (ep.isSneaking())
				((TileEntityDriver)te).decrement();
			else
				((TileEntityDriver)te).increment();
			return true;
		case EFFECTOR:
			ep.openGui(ExpandedRedstone.instance, 0, world, x, y, z);
			return true;
		case PLACER:
			ep.openGui(ExpandedRedstone.instance, 0, world, x, y, z);
			return true;
		case PROXIMITY:
			((TileEntityProximity)te).stepCreature();
			return true;
		default:
			return false;
		}
	}

	@Override
	public Icon getBlockTexture(IBlockAccess iba, int x, int y, int z, int s)
	{
		ExpandedRedstoneTileEntity te = (ExpandedRedstoneTileEntity)iba.getBlockTileEntity(x, y, z);
		int meta = iba.getBlockMetadata(x, y, z);
		if (te == null)
			return null;
		RedstoneTiles r = RedstoneTiles.TEList[meta];
		if (te.isOverridingIcon(s))
			return te.getOverridingIcon(s);
		if (r.isThinTile()) {
			if (s == 1)
				return icons[s][meta][te.getTopTexture()];
			return icons[s][meta][te.getTextureForSide(s)];
		}
		else if (!r.isOmniTexture()) {
			if (te.getFacing() != null && s == te.getFacing().ordinal()) {
				return front[meta][te.getFrontTexture()];
			}
			else {
				return icons[s][meta][te.getTextureForSide(s)];
			}
		}
		else
			return icons[s][meta][te.getTextureForSide(s)];
	}

	@Override
	public Icon getIcon(int s, int meta)
	{
		RedstoneTiles r = RedstoneTiles.TEList[meta];
		if (s == 4 && !r.isThinTile() && !r.isOmniTexture()) {
			if (r == RedstoneTiles.BREAKER)
				return front[meta][4];
			else
				return front[meta][0];
		}
		return icons[s][meta][0];
	}

	private void registerBlankTextures(IconRegister ico) {
		for (int i = 0; i < icons.length; i++) {
			for (int j = 0; j < icons[i].length; j++) {
				for (int k = 0; k < icons[i][j].length; k++) {
					if (RedstoneTiles.TEList[j].isThinTile()) {
						if (i == 0 || i == 1)
							icons[i][j][k] = ico.registerIcon(BLANK_TEX);
						else
							icons[i][j][k] = ico.registerIcon(BLANK_TEX_2);
					}
					else {
						icons[i][j][k] = ico.registerIcon("furnace_top");
					}
				}
			}
		}

		for (int i = 0; i < front.length; i++) {
			for (int j = 0; j < front[i].length; j++) {
				front[i][j] = ico.registerIcon("furnace_top");
			}
		}
	}

	@Override
	public void registerIcons(IconRegister ico)
	{
		this.registerBlankTextures(ico);
		trans = ico.registerIcon("ExpandedRedstone:trans");

		for (int i = 0; i < RedstoneTiles.TEList.length; i++) {
			RedstoneTiles r = RedstoneTiles.TEList[i];
			int num = r.getTextureStates();
			if (r.isThinTile()) {
				if (r.hasVariableTopTexture()) {
					for (int j = 0; j < num; j++) {
						icons[1][i][j] = ico.registerIcon("ExpandedRedstone:"+r.name().toLowerCase()+"_top"+"_"+j);
						ExpandedRedstone.logger.log("Creating variable tile icon "+icons[1][i][j].getIconName()+" for "+r+"[1]["+i+"]["+j+"]");
					}
				}
				else {
					icons[1][i][0] = ico.registerIcon("ExpandedRedstone:"+r.name().toLowerCase()+"_top");
					ExpandedRedstone.logger.log("Creating static tile icon "+icons[1][i][0].getIconName()+" for "+r);
				}
			}
			else if (!r.isOmniTexture()) {
				if (r.isVariableTexture()) {
					for (int j = 0; j < num; j++) {
						front[i][j] = ico.registerIcon("ExpandedRedstone:"+r.name().toLowerCase()+"_front_"+j);
						ExpandedRedstone.logger.log("Creating variable block icon "+front[i][j].getIconName()+" for "+r+"["+i+"]["+j+"]");
					}
				}
				else {
					front[i][0] = ico.registerIcon("ExpandedRedstone:"+r.name().toLowerCase()+"_front");
					ExpandedRedstone.logger.log("Creating static block icon "+front[i][0].getIconName()+" for "+r);
				}
			}
			if (r.isOmniTexture()) {
				for (int k = 0; k < 6; k++) {
					icons[k][i][0] = ico.registerIcon("ExpandedRedstone:"+r.name().toLowerCase());
				}
				ExpandedRedstone.logger.log("Creating static full texture "+icons[0][i][0].getIconName()+" for "+r);
			}
		}
	}
	/*
	@Override
	public final ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		int id = this.idPicked(world, x, y, z);
		if (id == 0)
			return null;
		int meta = world.getBlockMetadata(target.blockX, target.blockY, target.blockZ);
		return RedstoneItems.PLACER.getStackOfMetadata(meta);
	}*/

	@Override
	public final void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof IInventory)
			ReikaItemHelper.dropInventory(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}
	/*
	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();
		li.add(RedstoneItems.PLACER.getStackOfMetadata(meta));
		return li;
	}*/

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		int meta = iba.getBlockMetadata(x, y, z);
		RedstoneTiles r = RedstoneTiles.TEList[meta];
		if (r == RedstoneTiles.CAMOFLAGE) {
			TileEntityCamo tc = (TileEntityCamo)iba.getBlockTileEntity(x, y, z);
			AxisAlignedBB box = tc.getBoundingBox();
			if (box == null)
				this.setBlockBounds(0, 0, 0, 0, 0, 0);
			else
				this.setBlockBounds((float)box.minX, (float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.maxY, (float)box.maxZ);
		}
		else if (r.isThinTile())
			this.setBlockBounds(0, 0, 0, 1, 0.1875F, 1);
		else
			this.setBlockBounds(0, 0, 0, 1, 1, 1);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		RedstoneTiles r = RedstoneTiles.TEList[meta];
		if (r == RedstoneTiles.CAMOFLAGE) {
			TileEntityCamo tc = (TileEntityCamo)world.getBlockTileEntity(x, y, z);
			AxisAlignedBB box = tc.getBoundingBox();
			if (box == null)
				return null;
			else
				return box.offset(x, y, z);
		}
		else
			return AxisAlignedBB.getAABBPool().getAABB(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
	}

	@Override
	public int getRenderType() {
		return ExpandedRedstone.proxy.tileRender;
	}

	public Icon getFrontTexture(IBlockAccess iba, int x, int y, int z) {
		ExpandedRedstoneTileEntity te = (ExpandedRedstoneTileEntity)iba.getBlockTileEntity(x, y, z);
		return front[iba.getBlockMetadata(x, y, z)][te.getFrontTexture()];
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition tg, World world, int x, int y, int z)
	{
		return this.getBlockDropped(world, tg.blockX, tg.blockY, tg.blockZ, world.getBlockMetadata(tg.blockX, tg.blockY, tg.blockZ), 0).get(0);
	}

}
