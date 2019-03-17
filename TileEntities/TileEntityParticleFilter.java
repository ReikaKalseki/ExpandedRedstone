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

import java.util.HashSet;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityParticleFilter extends TileRedstoneBase implements LocationCached {

	public static final int MAX_RANGE = 16;
	public static final double MAX_FRACTION = 0.75F;

	private int range = 4;

	private double filterFraction;

	private static final HashSet<WorldLocation> cache = new HashSet();

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);

		filterFraction = world.getBlockPowerInput(x, y, z)/15D*MAX_FRACTION;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		cache.add(new WorldLocation(this));
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.PARTICLE.ordinal();
	}

	public void stepRange(EntityPlayer ep) {
		range++;
		if (range > MAX_RANGE)
			range = 1;
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.5F);
		ReikaChatHelper.sendChatToPlayer(ep, "Detector range set to "+range+"m.");
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		range = NBT.getInteger("rng");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("rng", range);
	}

	@Override
	public int getTopTexture() {
		return filterFraction > 0 ? 1 : 0;
	}

	@Override
	public void breakBlock() {
		cache.remove(new WorldLocation(this));
	}

	@SideOnly(Side.CLIENT)
	public static boolean cullParticle(EntityFX fx) {
		if (cache.isEmpty())
			return false;
		WorldLocation c = new WorldLocation(fx);
		for (WorldLocation loc : cache) {
			if (loc.isWithinSquare(c, MAX_RANGE)) {
				TileEntity te = loc.getTileEntity();
				if (te instanceof TileEntityParticleFilter) {
					TileEntityParticleFilter tp = (TileEntityParticleFilter)te;
					if (loc.isWithinSquare(c, tp.range)) {
						if (ReikaRandomHelper.doWithChance(tp.filterFraction))
							return true;
					}
				}
			}
		}
		return false;
	}
}
