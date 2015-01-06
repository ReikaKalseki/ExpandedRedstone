/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.ItemBlocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import Reika.ExpandedRedstone.TileEntities.TileEntityBreaker;
import Reika.ExpandedRedstone.TileEntities.TileEntityBreaker.Materials;
import Reika.ExpandedRedstone.TileEntities.TileEntityShockPanel;
import Reika.ExpandedRedstone.TileEntities.TileEntityShockPanel.Lens;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCircuitPlacer extends Item {

	private int index;

	public ItemCircuitPlacer() {
		super();
		hasSubtypes = true;
		this.setMaxDamage(0);
		this.setCreativeTab(ExpandedRedstone.tab);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava) {
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
			if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava)
				return false;
		}
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
		List inblock = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		if (inblock.size() > 0)
			return false;
		RedstoneTiles tile = RedstoneTiles.TEList[is.getItemDamage()];
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		else
		{
			if (!ep.capabilities.isCreativeMode)
				--is.stackSize;
			world.setBlock(x, y, z, tile.getBlock(), tile.getBlockMetadata(), 3);
		}
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, "step.stone", 1F, 1.5F);
		TileRedstoneBase te = (TileRedstoneBase)world.getTileEntity(x, y, z);
		if (tile.isDirectionable()) {
			if (tile.isReversedPlacement()) {
				ForgeDirection dir = ReikaPlayerAPI.getDirectionFromPlayerLook(ep, tile.canBeVertical());
				te.setFacing(dir.getOpposite());
			}
			else {
				te.setFacing(ReikaPlayerAPI.getDirectionFromPlayerLook(ep, tile.canBeVertical()));
			}
		}
		else
			te.setFacing(ForgeDirection.UNKNOWN);

		te.setPlacer(ep);
		if (tile == RedstoneTiles.BREAKER) {
			TileEntityBreaker brk = (TileEntityBreaker)te;
			if (is.stackTagCompound != null) {
				int level = is.stackTagCompound.getInteger("nbt");
				int dura = is.stackTagCompound.getInteger("dmg");
				brk.setHarvestLevel(level);
				brk.setDurability(dura);
			}
		}
		if (tile == RedstoneTiles.SHOCK) {
			TileEntityShockPanel shk = (TileEntityShockPanel)te;
			if (is.stackTagCompound != null) {
				int level = is.stackTagCompound.getInteger("nbt");
				shk.setDamageLevel(level);
			}
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item id, CreativeTabs tab, List list) {
		for (int i = 0; i < RedstoneTiles.TEList.length; i++) {
			ItemStack item = new ItemStack(id, 1, i);
			if (i == RedstoneTiles.BREAKER.ordinal()) {
				for (int h = 0; h < Materials.mats.length; h++) {
					ItemStack item1 = item.copy();
					item1.stackTagCompound = new NBTTagCompound();
					item1.stackTagCompound.setInteger("nbt", h);
					if (h == 0)
						item1.stackTagCompound.setInteger("dmg", 128);
					list.add(item1);
				}
			}
			else if (i == RedstoneTiles.SHOCK.ordinal()) {
				for (int h = 0; h < Lens.list.length; h++) {
					ItemStack item1 = item.copy();
					item1.stackTagCompound = new NBTTagCompound();
					item1.stackTagCompound.setInteger("nbt", h);
					list.add(item1);
				}
			}
			else
				list.add(item);
		}
	}

	public void setIndex(int a) {
		index = a;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void registerIcons(IIconRegister ico) {}

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
		if (is.stackTagCompound != null) {
			if (tile == RedstoneTiles.BREAKER) {
				int level = is.stackTagCompound.getInteger("nbt");
				int dura = is.stackTagCompound.getInteger("dmg");
				Materials mat = TileEntityBreaker.Materials.mats[level];
				li.add(String.format("Harvest Level: %s", mat.getName()));
				if (mat == Materials.WOOD) {
					li.add(String.format("Durability: %d", dura));
					if (dura == 0)
						li.add("Breaker is worn out!");
				}
			}
			if (tile == RedstoneTiles.SHOCK) {
				int level = is.stackTagCompound.getInteger("nbt");
				Lens mat = Lens.list[level];
				li.add(String.format("Lens Type: %s", mat.name()));
				if (mat == Lens.STAR)
					li.add(String.format("Attack Damage: Instant-Kill"));
				else
					li.add(String.format("Attack Damage: %.1f Hearts", mat.attackDamage/2F));
				li.add(String.format("Attack Range: %d", mat.attackRange));
			}
		}
	}

	@Override
	public final String getItemStackDisplayName(ItemStack is) {
		return RedstoneTiles.TEList[is.getItemDamage()].getName();
	}

}
