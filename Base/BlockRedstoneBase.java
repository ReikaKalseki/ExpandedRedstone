/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.Base;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModRegistry.InterfaceCache;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import Reika.ExpandedRedstone.TileEntities.TileEntity555;
import Reika.ExpandedRedstone.TileEntities.TileEntityAnalogTransmitter;
import Reika.ExpandedRedstone.TileEntities.TileEntityArithmetic;
import Reika.ExpandedRedstone.TileEntities.TileEntityBreaker;
import Reika.ExpandedRedstone.TileEntities.TileEntityCamo;
import Reika.ExpandedRedstone.TileEntities.TileEntityChestReader;
import Reika.ExpandedRedstone.TileEntities.TileEntityCountdown;
import Reika.ExpandedRedstone.TileEntities.TileEntityDriver;
import Reika.ExpandedRedstone.TileEntities.TileEntityEqualizer;
import Reika.ExpandedRedstone.TileEntities.TileEntityProximity;
import Reika.ExpandedRedstone.TileEntities.TileEntityRedstoneRelay;
import Reika.ExpandedRedstone.TileEntities.TileEntityShockPanel;
import Reika.ExpandedRedstone.TileEntities.TileEntitySignalScaler;
import Reika.RotaryCraft.API.ItemFetcher;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public abstract class BlockRedstoneBase extends Block implements IWailaDataProvider {

	public static IIcon trans;
	private static IIcon[][][] icons;
	private static IIcon[][] front;
	private static final String BLANK_TEX = "ExpandedRedstone:basic";
	private static final String BLANK_TEX_2 = "ExpandedRedstone:basic_side";
	private static final String BLANK_TEX_3 = "ExpandedRedstone:block";

	public BlockRedstoneBase(Material mat) {
		super(mat);
		this.setCreativeTab(null);
		this.setHardness(0.75F);
		this.setResistance(2.5F);
		this.setLightOpacity(0);
	}

	@Override
	public final TileEntity createTileEntity(World world, int meta) {
		return RedstoneTiles.createTEFromIDandMetadata(this, meta);
	}

	@Override
	public final boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public final ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();
		RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
		if (r == null)
			return li;
		if (world.getBlock(x, y, z) != this)
			return li;
		ItemStack is = r.getItem();
		if (r == RedstoneTiles.BREAKER) {
			TileEntityBreaker te = (TileEntityBreaker)world.getTileEntity(x, y, z);
			if (te != null) {
				is.stackTagCompound = new NBTTagCompound();
				is.stackTagCompound.setInteger("nbt", te.getHarvestLevel());
				is.stackTagCompound.setInteger("dmg", te.getDurability());
			}
			else
				return li;
		}
		if (r == RedstoneTiles.SHOCK) {
			TileEntityShockPanel te = (TileEntityShockPanel)world.getTileEntity(x, y, z);
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
	public final boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harvest)
	{
		if (!player.capabilities.isCreativeMode)
			this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
		return world.setBlockToAir(x, y, z);
	}

	@Override
	public final int isProvidingWeakPower(IBlockAccess iba, int x, int y, int z, int s)
	{
		TileRedstoneBase te = (TileRedstoneBase)iba.getTileEntity(x, y, z);
		if (te.canPowerSide(s)) {
			if (te.isBinaryRedstone())
				return te.isEmitting() ? 15 : 0;
			else
				return te.getEmission();
		}
		else
			return 0;
	}

	@Override
	public final void onNeighborBlockChange(World world, int x, int y, int z, Block neighborID) {
		RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
		switch (r) {
			case COLUMN:
				world.notifyBlocksOfNeighborChange(x, y+1, z, this, 0);
				break;
			case ANALOGTRANSMITTER:
				TileEntityAnalogTransmitter te = (TileEntityAnalogTransmitter)world.getTileEntity(x, y, z);
				te.markRecalculationIn(2);
				break;
			default:
				break;
		}
	}

	@Override
	public final int isProvidingStrongPower(IBlockAccess iba, int x, int y, int z, int s) {
		TileRedstoneBase te = (TileRedstoneBase)iba.getTileEntity(x, y, z);
		return te.canProvideStrongPower() ? this.isProvidingWeakPower(iba, x, y, z, s) : 0;
	}

	@Override
	public final boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z)
	{
		return false;
	}

	@Override
	public final boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata)
	{
		return false;
	}

	@Override
	public final int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
	{
		return 0;
	}

	@Override
	public final boolean canSilkHarvest() {
		return false;
	}

	@Override
	public final boolean canProvidePower() {
		return true;
	}
	/*
	@Override
	public boolean shouldCheckWeakPower(World world, int x, int y, int z, int side)
	{
		return true;
	}*/

	@Override
	public final boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int par6, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
		if (ep.isSneaking() && !r.hasSneakActions())
			return false;
		TileRedstoneBase te = (TileRedstoneBase)world.getTileEntity(x, y, z);
		if (te == null)
			return false;
		te.syncAllData(true);
		if (is != null && InterfaceCache.IWRENCH.instanceOf(is.getItem())) {
			te.rotate();
			return true;
		}
		if (ModList.ROTARYCRAFT.isLoaded() && ItemFetcher.isPlayerHoldingAngularTransducer(ep))
			return false;
		switch (r) {
			case CHESTREADER:
				((TileEntityChestReader)te).alternate();
				te.syncAllData(true);
				return true;
			case CLOCK:
				((TileEntity555)te).incrementSetting(ep.isSneaking());
				te.syncAllData(true);
				return true;
			case DRIVER:
				if (ep.isSneaking())
					((TileEntityDriver)te).decrement();
				else
					((TileEntityDriver)te).increment();
				te.syncAllData(true);
				return true;
			case ARITHMETIC:
				((TileEntityArithmetic)te).stepMode();
				return true;
			case RELAY:
				((TileEntityRedstoneRelay)te).toggle();
				return true;
			case EFFECTOR:
			case PLACER:
			case ANALOGTRANSMITTER:
			case ANALOGRECEIVER:
				ep.openGui(ExpandedRedstone.instance, 0, world, x, y, z);
				return true;
			case PROXIMITY:
				if (ep.isSneaking())
					((TileEntityProximity)te).stepRange();
				else
					((TileEntityProximity)te).stepCreature();
				te.syncAllData(true);
				return true;
			case SCALER:
				if (ep.isSneaking())
					((TileEntitySignalScaler)te).incrementMinValue();
				else
					((TileEntitySignalScaler)te).incrementMaxValue();
				te.syncAllData(true);
				return true;
			case EQUALIZER:
				int n = ep.isSneaking() ? 10 : 1;
				for (int i = 0; i < n; i++)
					((TileEntityEqualizer)te).incrementValue();
				te.syncAllData(true);
				return true;
			case COUNTDOWN:
				if (ep.isSneaking()) {
					((TileEntityCountdown)te).resetTimer();
				}
				else {
					((TileEntityCountdown)te).incrementDelay();
				}
				return true;
			default:
				return false;
		}
	}

	@Override
	public final IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s)
	{
		TileRedstoneBase te = (TileRedstoneBase)iba.getTileEntity(x, y, z);
		int meta = iba.getBlockMetadata(x, y, z);
		if (te == null)
			return null;
		RedstoneTiles r = RedstoneTiles.getTEAt(iba, x, y, z);
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		if (te.isOverridingIcon(s))
			return te.getOverridingIcon(s);
		if (r.isThinTile()) {
			if (s == 1)
				return icons[s][r.ordinal()][te.getTopTexture()];
			return icons[s][r.ordinal()][te.getTextureForSide(s)];
		}
		else if (r.hasHardcodedDirectionTexture(dir)) {
			return icons[s][r.ordinal()][te.getTextureForSide(s)];
		}
		else if (!r.isOmniTexture()) {
			if (te.getFacing() != null && s == te.getFacing().ordinal()) {
				//ReikaJavaLibrary.pConsole(front[r.ordinal()][te.getFrontTexture()]);
				return front[r.ordinal()][te.getFrontTexture()];
			}
			else {
				return icons[s][r.ordinal()][te.getTextureForSide(s)];
			}
		}
		else
			return icons[s][r.ordinal()][te.getTextureForSide(s)];
	}

	@Override
	public final IIcon getIcon(int s, int meta)
	{
		RedstoneTiles r = RedstoneTiles.TEList[meta];
		if (s == 4 && !r.isThinTile() && !r.isOmniTexture()) {
			if (r == RedstoneTiles.BREAKER)
				return front[r.ordinal()][4];
			else
				return front[r.ordinal()][0];
		}
		return icons[s][r.ordinal()][0];
	}

	private final void registerBlankTextures(IIconRegister ico) {
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
						icons[i][j][k] = ico.registerIcon(BLANK_TEX_3);
					}
				}
			}
		}

		for (int i = 0; i < front.length; i++) {
			for (int j = 0; j < front[i].length; j++) {
				front[i][j] = ico.registerIcon(BLANK_TEX_3);
			}
		}
	}

	@Override
	public final void registerBlockIcons(IIconRegister ico)
	{
		int max = 0;
		for (int i = 0; i < RedstoneTiles.TEList.length; i++) {
			RedstoneTiles r = RedstoneTiles.TEList[i];

			max = Math.max(max, r.getTextureStates());
		}

		icons = new IIcon[6][RedstoneTiles.TEList.length][max];
		front = new IIcon[RedstoneTiles.TEList.length][max];
		this.registerBlankTextures(ico);
		trans = ico.registerIcon("ExpandedRedstone:trans");

		for (int i = 0; i < RedstoneTiles.TEList.length; i++) {
			RedstoneTiles r = RedstoneTiles.TEList[i];

			int num = r.getTextureStates();
			String pfx = "ExpandedRedstone:"+r.name().toLowerCase(Locale.ENGLISH);
			String pre = num > 1 ? pfx+"/" : pfx+"_";

			if (r.isThinTile()) {
				if (r.hasVariableTopTexture()) {
					for (int j = 0; j < num; j++) {
						icons[1][i][j] = ico.registerIcon(pre+"top"+"_"+j);
						ExpandedRedstone.logger.debug("Creating variable tile icon "+icons[1][i][j].getIconName()+" for "+r+"[1]["+i+"]["+j+"]");
					}
				}
				else {
					icons[1][i][0] = ico.registerIcon(pre+"top");
					ExpandedRedstone.logger.debug("Creating static tile icon "+icons[1][i][0].getIconName()+" for "+r);
				}
			}
			else if (r.hasHardcodedDirectionTextures()) {
				for (int j = 0; j < 6; j++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[j];
					if (r.hasHardcodedDirectionTexture(dir)) {
						icons[j][i][0] = ico.registerIcon(pre+"_"+dir.name().toLowerCase());
						ExpandedRedstone.logger.log("Creating directionable block icon "+icons[j][i][0].getIconName()+" for "+r+"["+j+"]["+i+"][0]");
					}
				}
			}
			else if (!r.isOmniTexture()) {
				if (r.isVariableTexture()) {
					for (int j = 0; j < num; j++) {
						front[i][j] = ico.registerIcon(pre+"front_"+j);
						ExpandedRedstone.logger.debug("Creating variable block icon "+front[i][j].getIconName()+" for "+r+"["+i+"]["+j+"]");
					}
				}
				else {
					front[i][0] = ico.registerIcon(pre+"front");
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
	public final void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IInventory)
			ReikaItemHelper.dropInventory(world, x, y, z);
		if (te instanceof AnalogWireless)
			((AnalogWireless)te).remove();
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public abstract void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z);

	@Override
	public abstract AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z);

	@Override
	public int getRenderType() {
		return ExpandedRedstone.proxy.tileRender;
	}

	public final IIcon getFrontTexture(IBlockAccess iba, int x, int y, int z) {
		TileRedstoneBase te = (TileRedstoneBase)iba.getTileEntity(x, y, z);
		return front[iba.getBlockMetadata(x, y, z)][te.getFrontTexture()];
	}

	@Override
	public final ItemStack getPickBlock(MovingObjectPosition tg, World world, int x, int y, int z)
	{
		return this.getDrops(world, tg.blockX, tg.blockY, tg.blockZ, world.getBlockMetadata(tg.blockX, tg.blockY, tg.blockZ), 0).get(0);
	}

	@Override
	@ModDependent(ModList.WAILA)
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler config) {
		World world = acc.getWorld();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
			if (r == RedstoneTiles.CAMOFLAGE) {
				TileEntityCamo te = (TileEntityCamo)acc.getTileEntity();
				BlockKey id = te.getImitatedBlockID();
				if (id != null) {
					if (id.blockID == Blocks.grass && !te.canRenderAsGrass())
						id = new BlockKey(Blocks.dirt);
					return id.asItemStack();
				}
			}
		}
		return null;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaHead(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		/*
		World world = acc.getWorld();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			RedstoneTiles r = RedstoneTiles.getTEAt(world, x, y, z);
			tip.add(EnumChatFormatting.WHITE+this.getPickBlock(mov, world, x, y, z).getDisplayName());
		}*/
		return tip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		TileEntity te = acc.getTileEntity();
		((TileEntityBase)te).syncAllData(false);
		if (te instanceof TileEntitySignalScaler) {
			TileEntitySignalScaler sc = (TileEntitySignalScaler)te;
			tip.add("Scaling inputs to ["+sc.getMinValue()+"-"+sc.getMaxValue()+"]");
		}
		if (te instanceof TileEntityEqualizer) {
			TileEntityEqualizer eq = (TileEntityEqualizer)te;
			tip.add("Watching note "+eq.getPitch());
		}
		if (te instanceof TileEntityCountdown) {
			TileEntityCountdown cd = (TileEntityCountdown)te;
			tip.add("Time Remaining: "+cd.getCountdownDisplay());
		}
		if (te instanceof TileEntityArithmetic) {
			TileEntityArithmetic ar = (TileEntityArithmetic)te;
			tip.add("Mode: "+ReikaStringParser.capFirstChar(ar.getMode().name()));
			tip.add(ar.getFunction());
		}
		if (te instanceof TileEntity555) {
			TileEntity555 t5 = (TileEntity555)te;
			tip.add("Timing: "+t5.settingsToString());
		}
		return tip;
	}

	@ModDependent(ModList.WAILA)
	public final List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		String s1 = EnumChatFormatting.ITALIC.toString();
		String s2 = EnumChatFormatting.BLUE.toString();
		String mod = "Expanded Redstone";
		if (acc.getTileEntity() instanceof TileEntityCamo) {
			TileEntityCamo te = (TileEntityCamo)acc.getTileEntity();
			BlockKey id = te.getImitatedBlockID();
			if (id != null) {
				if (ReikaItemHelper.isVanillaBlock(id.blockID))
					mod = "Minecraft";
				else {
					UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(id.blockID);
					if (uid != null) {
						mod = Loader.instance().getIndexedModList().get(uid.modId).getName();
					}
				}
			}
		}
		//currenttip.add(s2+s1+mod);
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

	public static IIcon getIcon(int s, int tile, int idx) {
		return icons[s][tile][idx];
	}

}
