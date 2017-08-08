/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ExpandedRedstone.Base.InventoriedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityRedstonePump extends InventoriedRedstoneTileEntity {

	private final HybridTank tank = new HybridTank("rspump", 4000);
	private BlockArray blocks = new BlockArray();

	@Override
	public int getTEIndex() {
		return RedstoneTiles.PUMP.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		Block idbelow = world.getBlock(x, y-1, z);
		if (idbelow == Blocks.air)
			return;
		Block b = idbelow;
		Fluid f2 = FluidRegistry.lookupFluidForBlock(b);
		if (f2 == null)
			return;
		if (blocks.isEmpty()) {
			blocks.setLiquid(ReikaWorldHelper.getMaterial(world, x, y-1, z));
			blocks.recursiveAddLiquidWithBounds(world, x, y-1, z, x-16, 0, z-16, x+16, y-1, z+16);
			blocks.reverseBlockOrder();
		}

		if (!tank.isEmpty())
			this.tryDistributeFluid(world, x, y, z);

		//ReikaJavaLibrary.pConsole(inv[0]);
	}

	@Override
	protected void onPositiveRedstoneEdge() {
		int level = worldObj.getBlockPowerInput(xCoord, yCoord, zCoord);
		Coordinate c = blocks.getNextAndMoveOn();
		if (c == null)
			return;
		FluidStack f = ReikaWorldHelper.getDrainableFluid(worldObj, c.xCoord, c.yCoord, c.zCoord);
		if (f != null && tank.canTakeIn(f) && f.amount > 0) {
			tank.addLiquid(f.amount, f.getFluid());
			c.setBlock(worldObj, Blocks.air);
			worldObj.markBlockForUpdate(c.xCoord, c.yCoord, c.zCoord);
			if (RedstoneOptions.NOISES.getState())
				ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click");
		}
	}

	private void tryDistributeFluid(World world, int x, int y, int z) {
		if (tank.getLevel() >= 1000 && inv[0] != null) {
			ItemStack full = FluidContainerRegistry.fillFluidContainer(new FluidStack(tank.getActualFluid(), 1000), inv[0]);
			if (full != null) {
				inv[0] = full;
				tank.removeLiquid(1000);
				return;
			}
		}

		this.tryEjectFluid(world, x, y, z);
	}

	private void tryEjectFluid(World world, int x, int y, int z) {
		for (int i = 1; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			if (te instanceof IFluidHandler) {
				IFluidHandler ifl = (IFluidHandler)te;
				if (ifl.canFill(dir.getOpposite(), tank.getActualFluid())) {
					int rem = ifl.fill(dir.getOpposite(), tank.getFluid(), true);
					if (rem > 0)
						tank.removeLiquid(rem);
				}
			}
		}
	}

	@Override
	public int getTextureForSide(int s) {
		return s == ForgeDirection.DOWN.ordinal() ? 0 : 0;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return FluidContainerRegistry.isEmptyContainer(itemstack);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return !FluidContainerRegistry.isEmptyContainer(itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

}
