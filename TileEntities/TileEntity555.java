/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
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
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntity555 extends ExpandedRedstoneTileEntity {

	private StepTimer timer_on;
	private StepTimer timer_off;

	private Settings setting = Settings.L20;

	public static enum Settings {
		L5(5, 5),
		L10(10, 10),
		L20(20, 20),
		L50(50, 50),
		L100(100, 100),
		L300(300, 300),
		L1200(1200, 1200),
		L7200(72000, 72000);

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
		if (emit)
			timer_on.update();
		else
			timer_off.update();
		if (timer_on.checkCap())
			emit = false;
		if (timer_off.checkCap())
			emit = true;
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

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("set", setting.ordinal());
	}

	@Override
	public int getTopTexture() {
		return setting.ordinal();
	}
}
