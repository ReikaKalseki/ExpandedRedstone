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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaDateHelper;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntity555 extends TileRedstoneBase {

	private StepTimer timer_on;
	private StepTimer timer_off;

	private Settings settingOff = Settings.L20;
	private Settings settingOn = Settings.L20;

	public static enum Settings {
		L1(2), //0.1s (0.05 is 1/2 a redstone tick)
		L5(5), //0.25s
		L20(20), //1s
		L50(50), //2.5s
		L100(100), //5s
		L200(200), //10s
		L300(300), //15s
		L600(600), //30s
		L1200(1200), //1m
		L6000(6000), //5m
		L12000(12000), //10m
		L18000(18000), //15m
		L36000(36000), //30m
		L72000(72000), //1h
		L432000(432000); //6h

		public final int duration;

		public static final Settings[] list = values();

		private Settings(int d) {
			duration = d;
		}

		@Override
		public String toString() {
			return duration+" ticks ("+ReikaDateHelper.getTickAsHMS(duration)+")";
		}
	}

	public Settings getSettingOn() {
		return settingOn;
	}

	public Settings getSettingOff() {
		return settingOff;
	}

	public String settingsToString() {
		return settingOff.toString()+" off, "+settingOn.toString()+" on";
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		if (timer_off == null)
			timer_off = new StepTimer(settingOff.duration);
		if (timer_on == null)
			timer_on = new StepTimer(settingOn.duration);

		for (int i = 2; i < 6; i++)
			if (ReikaRedstoneHelper.isReceivingPowerFromRepeater(world, x, y, z, dirs[i]))
				return;

		if (this.isEmitting())
			timer_on.update();
		else
			timer_off.update();
		if (timer_on.checkCap()) {
			this.setEmitting(false);
			if (RedstoneOptions.NOISES.getState())
				ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.25F, 0.5F);
		}
		if (timer_off.checkCap()) {
			this.setEmitting(true);
			if (RedstoneOptions.NOISES.getState())
				ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.25F, 1F);
		}
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.CLOCK.ordinal();
	}

	public void incrementSetting(boolean on) {
		int o = on ? settingOn.ordinal() : settingOff.ordinal();
		o++;
		if (o >= Settings.list.length)
			o = 0;
		if (on)
			settingOn = Settings.list[o];
		else
			settingOff = Settings.list[o];
		this.loadSetting(Settings.list[o], on, true);
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.8F);
	}

	public void loadSetting(Settings s, boolean on, boolean chat) {
		if (on)
			this.setHiTime(s.duration);
		else
			this.setLoTime(s.duration);
		//ReikaChatHelper.clearChat();
		if (chat)
			ReikaChatHelper.write("Clock set to "+s.toString());
	}

	public void setLoTime(int time) {
		if (timer_off == null)
			timer_off = new StepTimer(time);
		timer_off.setCap(time);
	}

	public void setHiTime(int time) {
		if (timer_on == null)
			timer_on = new StepTimer(time);
		timer_on.setCap(time);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		settingOff = Settings.list[NBT.getInteger("set_off")];
		settingOn = Settings.list[NBT.getInteger("set_on")];
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("set_off", settingOff.ordinal());
		NBT.setInteger("set_on", settingOn.ordinal());
	}

	@Override
	public int[] getTopTextures() {
		return new int[]{0, 1+settingOff.ordinal(), 21+settingOn.ordinal()};
	}

	/*

	public boolean canPowerSide(int s) {
	if (this.getFacing() == null)
	return false;
		return s == this.getFacing().ordinal() || s == this.getFacing().getOpposite().ordinal();
	}*/
}
