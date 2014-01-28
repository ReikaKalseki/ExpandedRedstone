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
import java.util.List;

import mcp.mobius.waila.api.IWailaBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import Reika.ExpandedRedstone.TileEntities.TileEntity555;
import Reika.ExpandedRedstone.TileEntities.TileEntityBreaker;
import Reika.ExpandedRedstone.TileEntities.TileEntityCamo;
import Reika.ExpandedRedstone.TileEntities.TileEntityChestReader;
import Reika.ExpandedRedstone.TileEntities.TileEntityDriver;
import Reika.ExpandedRedstone.TileEntities.TileEntityProximity;
import Reika.ExpandedRedstone.TileEntities.TileEntityShockPanel;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRedTile extends Block implements IWailaBlock {

	public static Icon trans;
	private Icon[][][] icons = new Icon[6][RedstoneTiles.TEList.length][16];
	private Icon[][] front = new Icon[RedstoneTiles.TEList.length][16];
	private static final String BLANK_TEX = "ExpandedRedstone:basic";
	private static final String BLANK_TEX_2 = "ExpandedRedstone:basic_side";

	public BlockRedTile(int ID, Material mat) {
		super(ID, mat);
		this.setCreativeTab(ExpandedRedstone.tab);
		this.setHardness(0.75F);
		this.setResistance(2.5F);
		this.setLightOpacity(0);
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
		if (r == null)
			return li;
		if (world.getBlockId(x, y, z) != blockID)
			return li;
		ItemStack is = r.getItem();
		if (r == RedstoneTiles.BREAKER) {
			TileEntityBreaker te = (TileEntityBreaker)world.getBlockTileEntity(x, y, z);
			if (te != null) {
				is.stackTagCompound = new NBTTagCompound();
				is.stackTagCompound.setInteger("nbt", te.getHarvestLevel());
				is.stackTagCompound.setInteger("dmg", te.getDurability());
			}
			else
				return li;
		}
		if (r == RedstoneTiles.SHOCK) {
			TileEntityShockPanel te = (TileEntityShockPanel)world.getBlockTileEntity(x, y, z);
			if (te != null) {
				is.stackTagCompound = new NBTTagCompound();
				is.stackTagCompound.setInteger("nbt", te.getLensType().ordinal());
			}
			else
				return li;
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
		ExpandedRedstoneTileEntity te = (ExpandedRedstoneTileEntity)iba.getBlockTileEntity(x, y, z);
		return te.canProvideStrongPower() ? this.isProvidingWeakPower(iba, x, y, z, s) : 0;
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
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		if (te.isOverridingIcon(s))
			return te.getOverridingIcon(s);
		if (r.isThinTile()) {
			if (s == 1)
				return icons[s][meta][te.getTopTexture()];
			return icons[s][meta][te.getTextureForSide(s)];
		}
		else if (r.hasHardcodedDirectionTexture(dir)) {
			return icons[s][r.ordinal()][te.getTextureForSide(s)];
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
						ExpandedRedstone.logger.debug("Creating variable tile icon "+icons[1][i][j].getIconName()+" for "+r+"[1]["+i+"]["+j+"]");
					}
				}
				else {
					icons[1][i][0] = ico.registerIcon("ExpandedRedstone:"+r.name().toLowerCase()+"_top");
					ExpandedRedstone.logger.debug("Creating static tile icon "+icons[1][i][0].getIconName()+" for "+r);
				}
			}
			else if (r.hasHardcodedDirectionTextures()) {
				for (int j = 0; j < 6; j++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[j];
					if (r.hasHardcodedDirectionTexture(dir)) {
						icons[j][i][0] = ico.registerIcon("ExpandedRedstone:"+r.name().toLowerCase()+"_"+dir.name().toLowerCase());
						ExpandedRedstone.logger.log("Creating directionable block icon "+icons[j][i][0].getIconName()+" for "+r+"["+j+"]["+i+"][0]");
					}
				}
			}
			else if (!r.isOmniTexture()) {
				if (r.isVariableTexture()) {
					for (int j = 0; j < num; j++) {
						front[i][j] = ico.registerIcon("ExpandedRedstone:"+r.name().toLowerCase()+"_front_"+j);
						ExpandedRedstone.logger.debug("Creating variable block icon "+front[i][j].getIconName()+" for "+r+"["+i+"]["+j+"]");
					}
				}
				else {
					front[i][0] = ico.registerIcon("ExpandedRedstone:"+r.name().toLowerCase()+"_front");
					ExpandedRedstone.logger.debug("Creating static block icon "+front[i][0].getIconName()+" for "+r);
				}
			}
			if (r.isOmniTexture()) {
				for (int k = 0; k < 6; k++) {
					icons[k][i][0] = ico.registerIcon("ExpandedRedstone:"+r.name().toLowerCase());
				}
				ExpandedRedstone.logger.debug("Creating static full texture "+icons[0][i][0].getIconName()+" for "+r);
			}
		}
	}

	@Override
	public final void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof IInventory)
			ReikaItemHelper.dropInventory(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}

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
		int id = world.getBlockId(x, y, z);
		if (id != blockID)
			return id == 0 ? null : Block.blocksList[id].getCollisionBoundingBoxFromPool(world, x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (meta < 0 || meta >= RedstoneTiles.TEList.length)
			meta = 0;
		RedstoneTiles r = RedstoneTiles.TEList[meta];
		if (r == RedstoneTiles.CAMOFLAGE) {
			TileEntityCamo tc = (TileEntityCamo)world.getBlockTileEntity(x, y, z);
			AxisAlignedBB box = tc.getBoundingBox();
			if (box == null)
				return null;
			else
				return box.offset(x, y, z);
		}
		else if (r.isThinTile()) {
			return AxisAlignedBB.getAABBPool().getAABB(x, y, z, x+1, y+0.1875, z);
		}
		else
			return ReikaAABBHelper.getBlockAABB(x, y, z);
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

	@Override
	public int getLightOpacity(World world, int x, int y, int z)
	{
		RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
		if (r == RedstoneTiles.CAMOFLAGE) {
			boolean pwr = world.isBlockIndirectlyGettingPowered(x, y, z);
			int idb = world.getBlockId(x, y-1, z);
			if (idb == 0)
				return 0;
			return pwr ? Block.blocksList[idb].getLightOpacity(world, x, y-1, z) : 0;
		}
		return r.isOpaque() ? 255 : 0;
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler config) {
		World world = acc.getWorld();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
			if (r == RedstoneTiles.CAMOFLAGE) {
				if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
					TileEntityCamo te = (TileEntityCamo)acc.getTileEntity();
					int id = te.getImitatedBlockID();
					if (id > 0) {
						Block b = Block.blocksList[id];
						if (b != null) {
							return new ItemStack(b, 1, world.getBlockMetadata(x, y-1, z));
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		World world = acc.getWorld();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
			if (r == RedstoneTiles.CAMOFLAGE) {
				if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
					TileEntityCamo te = (TileEntityCamo)acc.getTileEntity();
					int id = te.getImitatedBlockID();
					if (id > 0) {
						Block b = Block.blocksList[id];
						if (b != null) {
							ItemStack mimic = new ItemStack(b, 1, world.getBlockMetadata(x, y-1, z));
							return ReikaJavaLibrary.makeListFrom(EnumChatFormatting.WHITE+mimic.getDisplayName());
						}
					}
					return new ArrayList();
				}
			}
		}
		return tip;
	}

	@Override
	public List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	public List<String> getWailaTail(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
		if (r == RedstoneTiles.CAMOFLAGE) {
			TileEntityCamo te = (TileEntityCamo)world.getBlockTileEntity(x, y, z);
			if (te.isOverridingIcon(0)) {
				int id = te.getImitatedBlockID();
				if (id > 0) {
					Block b = Block.blocksList[id];
					if (b != null) {
						return b.colorMultiplier(world, x, y-1, z);
					}
				}
				return super.colorMultiplier(world, x, y, z);
			}
		}
		return super.colorMultiplier(world, x, y, z);
	}

}
