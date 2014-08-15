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

import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class TileEntityProximity extends TileRedstoneBase {

	private int range = 16;

	private EntityType entity = EntityType.PLAYER;

	enum EntityType {
		PLAYER(EntityPlayer.class),
		MOB(EntityMob.class),
		ANIMAL(EntityAnimal.class),
		ALL(EntityLivingBase.class);

		private Class<? extends Entity> cl;

		public static final EntityType[] list = values();

		private EntityType(Class c) {
			cl = c;
		}

		public Class getEntityClass() {
			return cl;
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);

		if ((world.getTotalWorldTime()&3) == 0)
			this.findCreatures(world, x, y, z);
	}

	private void findCreatures(World world, int x, int y, int z) {
		Class c = entity.getEntityClass();
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1).expand(range, range, range);
		List<Entity> li = world.getEntitiesWithinAABB(c, box);
		for (int i = 0; i < li.size(); i++) {
			Entity e = li.get(i);
			double dd = ReikaMathLibrary.py3d(e.posX-x-0.5, e.posY-y-0.5, e.posZ-z-0.5);
			if (dd <= range) {
				this.setEmitting(true);
				return;
			}
		}
		this.setEmitting(false);
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.PROXIMITY.ordinal();
	}

	public void stepCreature() {
		int c = entity.ordinal();
		c++;
		if (c >= EntityType.list.length)
			c = 0;
		entity = EntityType.list[c];
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.5F);
	}

	public void stepRange() {
		range++;
		if (range > 24)
			range = 1;
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.5F);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		entity = EntityType.list[NBT.getInteger("type")];
		range = NBT.getInteger("rng");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("type", entity.ordinal());
		NBT.setInteger("rng", range);
	}

	@Override
	public int getTopTexture() {
		return entity.ordinal()*2+(this.isEmitting() ? 1 : 0);
	}
}