/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityProximity extends TileRedstoneBase {

	private int range = 16;
	private final StepTimer checkTimer = new StepTimer(5);
	private EntityType entity = EntityType.PLAYER;

	private static enum EntityType {
		PLAYER("Players", EntityPlayer.class),
		MOB("Hostiles", EntityMob.class),
		ANIMAL("Animals", EntityAnimal.class),
		ALL("All", EntityLivingBase.class),
		OWNER("Owner", null);

		private Class<? extends Entity> cl;
		public final String label;

		public static final EntityType[] list = values();

		private EntityType(String s, Class c) {
			cl = c;
			label = s;
		}

		public Class getEntityClass() {
			return cl;
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);

		checkTimer.update();
		if (checkTimer.checkCap())
			this.findCreatures(world, x, y, z);
	}

	private void findCreatures(World world, int x, int y, int z) {
		if (entity == EntityType.OWNER) {
			EntityPlayer ep = this.getPlacer();
			if (ep != null) {
				double dd = ReikaMathLibrary.py3d(ep.posX-x-0.5, ep.posY-y-0.5, ep.posZ-z-0.5);
				if (dd <= range) {
					this.setEmitting(true);
					return;
				}
			}
		}
		else {
			Class c = entity.getEntityClass();
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1).expand(range, range, range);
			List<Entity> li = world.getEntitiesWithinAABB(c, box);
			for (Entity e : li) {
				double dd = ReikaMathLibrary.py3d(e.posX-x-0.5, e.posY-y-0.5, e.posZ-z-0.5);
				if (dd <= range) {
					this.setEmitting(true);
					return;
				}
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
		ReikaChatHelper.write("Detector now senses "+entity.label+".");
	}

	public void stepRange() {
		range++;
		if (range > 24)
			range = 1;
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.5F);
		ReikaChatHelper.write("Detector range set to "+range+"m.");
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
