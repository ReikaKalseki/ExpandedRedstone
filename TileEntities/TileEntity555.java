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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaFormatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntity555 extends ExpandedRedstoneTileEntity {

	private StepTimer timer_on;
	private StepTimer timer_off;

	private Settings setting = Settings.L20;

	public static enum Settings {
		L5(5, 5), //0.25s
		//L10(10, 10),
		L20(20, 20), //1s
		L50(50, 50), //2.5s
		L100(100, 100), //5s
		L300(300, 300), //15s
		L1200(1200, 1200), //1m
		L18000(18000, 18000), //15m
		L7200(72000, 72000); //1h

		public final int low;
		public final int hi;

		public static final Settings[] list = values();

		private Settings(int lo, int h) {
			low = lo;
			hi = h;
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		if (timer_off == null)
			timer_off = new StepTimer(setting.low);
		if (timer_on == null)
			timer_on = new StepTimer(setting.hi);
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

	public void loadSettings() {
		this.setLoTime(setting.low);
		this.setHiTime(setting.hi);
		ReikaChatHelper.clearChat();
		ReikaChatHelper.write("Clock set to "+setting.low+" ticks ("+ReikaFormatHelper.getTickAsHMS(setting.low)+") off, "+setting.hi+" ticks ("+ReikaFormatHelper.getTickAsHMS(setting.hi)+") on.");
	}

	public void incrementSetting() {
		int o = setting.ordinal();
		o++;
		if (o >= Settings.list.length)
			o = 0;
		setting = Settings.list[o];
		this.loadSettings();
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.8F);
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
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		setting = Settings.list[NBT.getInteger("set")];
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("set", setting.ordinal());
	}

	@Override
	public int getTopTexture() {
		return setting.ordinal();
	}/*

	public boolean canPowerSide(int s) {
	if (this.getFacing() == null)
	return false;
		return s == this.getFacing().ordinal() || s == this.getFacing().getOpposite().ordinal();
	}*/
}
