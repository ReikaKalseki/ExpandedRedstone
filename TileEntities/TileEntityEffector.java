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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;
import Reika.ExpandedRedstone.Base.InventoriedRedstoneTileEntity;
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
		Class c = it.getClass();
		try {
			Method click = c.getMethod(this.getMethodName(), ItemStack.class, EntityPlayer.class, World.class, int.class, int.class, int.class, int.class, float.class, float.class, float.class);
			Object o = click.invoke(it, is, ep, world, dx, dy, dz, 0, 0F, 0F, 0F);
			inv[slot] = is;
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		catch (SecurityException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	//should see if can autoupdate/autoread xml conf files, too
	private String getMethodName() {
		return DragonAPICore.isDeObfEnvironment() ? "onItemUse" : "func_77648_a";
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.EFFECTOR.ordinal();
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public int getFrontTexture() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) ? 1 : 0;
	}
}
