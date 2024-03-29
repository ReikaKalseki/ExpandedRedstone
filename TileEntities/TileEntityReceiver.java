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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityReceiver extends TileRedstoneBase {

	private int[] target = {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};

	public static final int MAX_RANGE = 64;

	@Override
	public RedstoneTiles getTile() {
		return RedstoneTiles.RECEIVER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		this.drawBeam(world, x, y, z);
		this.readEmitter(world);
	}

	private void readEmitter(World world) {
		int x = target[0];
		int y = target[1];
		int z = target[2];
		if (x == Integer.MIN_VALUE || y == Integer.MIN_VALUE || z == Integer.MIN_VALUE) {
			this.setEmitting(false);
			return;
		}
		TileEntityEmitter te = (TileEntityEmitter)world.getTileEntity(x, y, z);
		if (te != null && te.getFacing() == this.getFacing().getOpposite() && te.isBeaming())
			this.setEmitting(true);
		else
			this.setEmitting(false);
	}

	private void drawBeam(World world, int x, int y, int z) {
		for (int i = 1; i <= MAX_RANGE; i++) {
			int dx = this.getFacingXScaled(i);
			int dy = this.getFacingYScaled(i);
			int dz = this.getFacingZScaled(i);
			Block b = world.getBlock(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (RedstoneTiles.getTEAt(world, dx, dy, dz) == RedstoneTiles.EMITTER) {
				target[0] = dx;
				target[1] = dy;
				target[2] = dz;
				return;
			}
			else if (b != Blocks.air) {
				if (b.getLightOpacity(world, dx, dy, dz) > 0) {
					for (int k = 0; k < 3; k++)
						target[k] = Integer.MIN_VALUE;
					return;
				}
			}
		}
		for (int k = 0; k < 3; k++)
			target[k] = Integer.MIN_VALUE;
		return;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		target = NBT.getIntArray("tg");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setIntArray("tg", target);
	}

	@Override
	public int getFrontTexture() {
		return this.isEmitting() ? 1 : 0;
	}

}
