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

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Interpolation;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityProximity extends TileRedstoneBase {

	private static final Interpolation signalCountCurve = new Interpolation(false);

	static {
		for (int i = 0; i <= 4; i++)
			signalCountCurve.addPoint(i, i);
		signalCountCurve.addPoint(6, 5);
		signalCountCurve.addPoint(8, 6);
		signalCountCurve.addPoint(10, 7);
		signalCountCurve.addPoint(12, 8);
		signalCountCurve.addPoint(15, 9);
		signalCountCurve.addPoint(18, 10);
		signalCountCurve.addPoint(21, 11);
		signalCountCurve.addPoint(24, 12);
		signalCountCurve.addPoint(32, 13);
		signalCountCurve.addPoint(40, 14);
		signalCountCurve.addPoint(64, 15);
	}

	private int range = 16;
	private final StepTimer checkTimer = new StepTimer(5);
	private EntityType entity = EntityType.OWNER;

	private int entityCount;
	private int signalPower;

	public static enum EntityType {
		OWNER("Owner", null),
		PLAYER("Players", EntityPlayer.class),
		MOB("Hostiles", EntityMob.class),
		ANIMAL("Animals", EntityAnimal.class),
		LIVING("Living", EntityLivingBase.class),
		ITEM("Items", EntityItem.class),
		ALL("Any", Entity.class),
		;

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

		public boolean isScaled() {
			return this != OWNER && this != ALL;
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);

		checkTimer.update();
		if (checkTimer.checkCap()) {
			this.findCreatures(world, x, y, z);
			int power = this.calculateSignalPower();
			if (power != signalPower) {
				signalPower = power;
				ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.5F+rand.nextFloat()*signalPower/15F*1.5F);
				this.update();
			}
		}
	}

	private int calculateSignalPower() {
		//int base = entityCount;// == 0 ? 0 : MathHelper.ceiling_double_int(Math.sqrt(entityCount));
		//return Math.min(15, base);
		return (int)signalCountCurve.getValue(entityCount);
	}

	private void findCreatures(World world, int x, int y, int z) {
		entityCount = 0;
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
					entityCount += this.getEntityCount(e);
				}
			}
		}
		this.setEmitting(entityCount > 0);
	}

	private int getEntityCount(Entity e) {
		return e instanceof EntityItem ? ((EntityItem)e).getEntityItem().stackSize : 1;
	}

	@Override
	public RedstoneTiles getTile() {
		return RedstoneTiles.PROXIMITY;
	}

	public void stepCreature(EntityPlayer ep) {
		int c = entity.ordinal();
		c++;
		if (c >= EntityType.list.length)
			c = 0;
		entity = EntityType.list[c];
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.5F);
		ReikaChatHelper.sendChatToPlayer(ep, "Detector now senses "+entity.label+".");
	}

	public void stepRange(EntityPlayer ep) {
		range++;
		if (range > 24)
			range = 1;
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.5F);
		ReikaChatHelper.sendChatToPlayer(ep, "Detector range set to "+range+"m.");
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		entity = EntityType.list[NBT.getInteger("type")];
		range = NBT.getInteger("rng");
		signalPower = NBT.getInteger("pwr");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("type", entity.ordinal());
		NBT.setInteger("rng", range);
		NBT.setInteger("pwr", signalPower);
	}

	@Override
	public int getTopTexture() {
		return entity.ordinal()*2+(this.isEmitting() ? 1 : 0);
	}

	@Override
	public boolean isBinaryRedstone() {
		return !entity.isScaled();
	}

	@Override
	public int getEmission() {
		return signalPower;
	}
}
