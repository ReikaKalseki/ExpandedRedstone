/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;
import Reika.ExpandedRedstone.Base.InventoriedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityRedstonePump extends InventoriedRedstoneTileEntity {

	private boolean lastPower;

	private HybridTank tank = new HybridTank("rspump", 4000);
	private BlockArray blocks = new BlockArray();

	@Override
	public int getTEIndex() {
		return RedstoneTiles.PUMP.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		int idbelow = world.getBlockId(x, y-1, z);
		if (idbelow == 0)
			return;
		Block b = Block.blocksList[idbelow];
		Fluid f2 = FluidRegistry.lookupFluidForBlock(b);
		if (f2 == null)
			return;
		if (blocks.isEmpty()) {
			blocks.setLiquid(world.getBlockMaterial(x, y-1, z));
			blocks.recursiveAddLiquidWithBounds(world, x, y-1, z, x-16, 0, z-16, x+16, y-1, z+16);
			blocks.reverseBlockOrder();
		}

		if (ReikaRedstoneHelper.isPositiveEdge(world, x, y, z, lastPower)) {
			int level = world.getBlockPowerInput(x, y, z);
			int[] xyz = blocks.getNextAndMoveOn();
			Fluid f = this.getLiquidHarvested(world, xyz[0], xyz[1], xyz[2]);
			if (f != null && this.canAccept(f)) {
				tank.addLiquid(1000, f);
				world.setBlock(xyz[0], xyz[1], xyz[2], 0);
				world.markBlockForUpdate(xyz[0], xyz[1], xyz[2]);
				if (RedstoneOptions.NOISES.getState())
					ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click");
			}
		}
		lastPower = world.isBlockIndirectlyGettingPowered(x, y, z);

		if (tank.getLevel() >= 1000 && inv[0] != null) {
			ItemStack full = FluidContainerRegistry.fillFluidContainer(new FluidStack(tank.getActualFluid(), 1000), inv[0]);
			if (full != null) {
				inv[0] = full;
				tank.removeLiquid(1000);
			}
		}

		//ReikaJavaLibrary.pConsole(inv[0]);
	}

	private boolean canAccept(Fluid f) {
		return tank.canTakeIn(1000) && (tank.isEmpty() || tank.getActualFluid().equals(f));
	}

	private Fluid getLiquidHarvested(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		Block b = Block.blocksList[id];
		Fluid f = FluidRegistry.lookupFluidForBlock(b);
		return f;
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
