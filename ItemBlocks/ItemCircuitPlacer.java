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

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Interfaces.IndexedItemSprites;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.ReikaWorldHelper;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneBlocks;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import Reika.ExpandedRedstone.TileEntities.TileEntityBreaker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCircuitPlacer extends ItemBlock implements IndexedItemSprites {

	private int index;

	public ItemCircuitPlacer(int id) {
		super(id);
		hasSubtypes = true;
		this.setMaxDamage(0);
		this.setCreativeTab(ExpandedRedstone.tab);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && world.getBlockMaterial(x, y, z) != Material.water && world.getBlockMaterial(x, y, z) != Material.lava) {
			if (side == 0)
				--y;
			if (side == 1)
				++y;
			if (side == 2)
				--z;
			if (side == 3)
				++z;
			if (side == 4)
				--x;
			if (side == 5)
				++x;
			if (!ReikaWorldHelper.softBlocks(world, x, y, z) && world.getBlockMaterial(x, y, z) != Material.water && world.getBlockMaterial(x, y, z) != Material.lava)
				return false;
		}
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
		List inblock = world.getEntitiesWithinAABB(EntityLiving.class, box);
		if (inblock.size() > 0)
			return false;
		RedstoneTiles tile = RedstoneTiles.TEList[is.getItemDamage()];
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		else
		{
			if (!ep.capabilities.isCreativeMode)
				--is.stackSize;
			world.setBlock(x, y, z, RedstoneBlocks.TILEENTITY.getBlockID(), is.getItemDamage(), 3);
		}
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, "step.stone", 1F, 1.5F);
		ExpandedRedstoneTileEntity te = (ExpandedRedstoneTileEntity)world.getBlockTileEntity(x, y, z);
		if (tile.isReversedPlacement()) {
			ForgeDirection dir = ReikaPlayerAPI.getDirectionFromPlayerLook(ep, tile.canBeVertical());
			if (dir.ordinal() < 2)
				te.setFacing(dir);
			else
				te.setFacing(dir.getOpposite());
		}
		else {
			te.setFacing(ReikaPlayerAPI.getDirectionFromPlayerLook(ep, tile.canBeVertical()));
		}
		te.placer = ep.getEntityName();
		if (tile == RedstoneTiles.BREAKER) {
			TileEntityBreaker brk = (TileEntityBreaker)te;
			if (is.stackTagCompound != null) {
				int level = is.stackTagCompound.getInteger("nbt");
				brk.setHarvestLevel(level);
			}
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for (int i = 0; i < RedstoneTiles.TEList.length; i++) {
			ItemStack item = new ItemStack(id, 1, i);
			list.add(item);
		}
	}

	@Override
	public int getItemSpriteIndex(ItemStack is) {
		return index;
	}

	public void setIndex(int a) {
		index = a;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void registerIcons(IconRegister ico) {}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public final String getUnlocalizedName(ItemStack is)
	{
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + String.valueOf(d);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean par4) {
		RedstoneTiles tile = RedstoneTiles.TEList[is.getItemDamage()];
		if (tile == RedstoneTiles.BREAKER && is.stackTagCompound != null) {
			int level = is.stackTagCompound.getInteger("nbt");
			li.add(String.format("Harvest Level: %d", level));
		}
	}

}
