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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneSounds;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityShockPanel extends TileRedstoneBase {

	public enum Lens {
		GLASS(1, 1),
		QUARTZ(2, 1),
		GLOWSTONE(1, 2),
		ENDER(2, 2),
		DIAMOND(4, 2),
		EMERALD(4, 3),
		STAR(Integer.MAX_VALUE, 5);

		public final int attackDamage;
		public final int attackRange;

		public static final Lens[] list = values();

		private Lens(int dmg, int range) {
			attackDamage = dmg;
			attackRange = range;
		}
	}

	private Lens lens;

	private boolean lastPower;

	@Override
	public int getTEIndex() {
		return RedstoneTiles.SHOCK.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (lens != null && ReikaRedstoneHelper.isPositiveEdge(world, x, y, z, lastPower)) {
			this.attack(world, x, y, z);
		}
		lastPower = world.isBlockIndirectlyGettingPowered(x, y, z);
	}

	private void attack(World world, int x, int y, int z) {
		AxisAlignedBB box = this.getBox(world, x, y, z);
		//ReikaJavaLibrary.pConsole(box);
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (EntityLivingBase e : li) {
			e.attackEntityFrom(DamageSource.cactus, this.getLensType().attackDamage);
		}
		for (int i = 0; i < 32; i++) {
			double rx = box.minX+rand.nextDouble()*(box.maxX - box.minX);
			double ry = box.minY+rand.nextDouble()*(box.maxY - box.minY);
			double rz = box.minZ+rand.nextDouble()*(box.maxZ - box.minZ);
			double dd = 1;
			double vx = this.getFacing().offsetX*dd;
			double vz = this.getFacing().offsetZ*dd;
			ReikaParticleHelper.CRITICAL.spawnAt(world, rx, ry, rz, vx, 0.2, vz);
		}

		//ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "expandedredstone:shock");
		if (world.isRemote)
			ReikaSoundHelper.playSound(RedstoneSounds.SHOCK, x+0.5, y+0.5, z+0.5, 1, 1);
	}

	private boolean canFire(World world, int x, int y, int z) {
		for (int i = 1; i <= this.getLensType().attackRange*2+1; i++) {
			int dx = this.getFacingXScaled(i);
			int dy = this.getFacingYScaled(i);
			int dz = this.getFacingZScaled(i);
			RedstoneTiles r = RedstoneTiles.getTEAt(world, dx, dy, dz);
			if (r == RedstoneTiles.SHOCK) {
				TileEntityShockPanel te = (TileEntityShockPanel)world.getTileEntity(dx, dy, dz);
				if (te.getFacing() != this.getFacing().getOpposite()) {
					return false;
				}
				else {
					boolean edge = ReikaRedstoneHelper.isPositiveEdge(world, dx, dy, dz, te.lastPower);
					if (edge)
						lastPower = false;
					return te.getLensType() == this.getLensType() && edge;
				}
			}
			else if (!ReikaWorldHelper.softBlocks(world, dx, dy, dz))
				return false;
		}
		return false;
	}

	private AxisAlignedBB getBox(World world, int x, int y, int z) {
		int d = this.getLensType().attackRange;

		ForgeDirection dir = this.getFacing();

		for (int i = 1; i < d; i++) {
			int dx = x+dir.offsetX*i;
			int dy = y+dir.offsetY*i;
			int dz = z+dir.offsetZ*i;
			Block b = world.getBlock(dx, dy, dz);
			if (b != Blocks.air) {
				if (b.isOpaqueCube() || b.getMaterial().isSolid()) {
					d = i;
					break;
				}
			}
		}

		int dx = -dir.offsetX*d;
		int dy = -dir.offsetY*d;
		int dz = -dir.offsetZ*d;

		int dx2 = dir.offsetX*d;
		int dy2 = dir.offsetY*d;
		int dz2 = dir.offsetZ*d;

		if (dx > 0)
			dx2 = 0;
		else
			dx = 0;
		if (dy > 0)
			dy2 = 0;
		else
			dy = 0;
		if (dz > 0)
			dz2 = 0;
		else
			dz = 0;

		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x-dx, y-dy, z-dz, x+1+dx2, y+1+dy2, z+1+dz2);
		//ReikaJavaLibrary.pConsole(box, Side.SERVER);
		return box;
	}

	public void setDamageLevel(Lens dmg) {
		lens = dmg;
	}

	public void setDamageLevel(int ordinal) {
		lens = Lens.list[ordinal];
	}

	public Lens getLensType() {
		return lens != null ? lens : Lens.GLASS;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		lens = Lens.list[NBT.getInteger("level")];
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		if (lens != null)
			NBT.setInteger("level", lens.ordinal());
	}

	@Override
	public int getFrontTexture() {
		return (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) ? 1 : 0);
	}

}
