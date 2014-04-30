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

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import Reika.RotaryCraft.API.Transducerable;

public class TileEntityWirelessAnalog extends ExpandedRedstoneTileEntity implements Transducerable {

	public static final int CHANNELS = 8192;

	private static final int[] channels = new int[CHANNELS]; //8192 channels
	private static final ArrayList<TileEntityWirelessAnalog>[] tiles = new ArrayList[CHANNELS];

	private int channel;

	public static void resetChannelData() {
		for (int i = 0; i < channels.length; i++) {
			channels[i] = 0;
			tiles[i] = null;
		}
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.ANALOG.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		//ReikaJavaLibrary.pConsole(channel, Side.SERVER);

		if (tiles[channel] == null || !tiles[channel].contains(this))
			this.add();
	}

	public int getChannel() {
		return channel;
	}

	public void recalculate() {
		this.recalculateChannel(channel);
		this.updateOthersOfChannel();
	}

	public void remove() {
		if (tiles[channel] != null)
			tiles[channel].remove(this);
		this.recalculate();
	}

	public void add() {
		if (tiles[channel] == null) {
			tiles[channel] = new ArrayList();
			tiles[channel].add(this);
		}
		else if (!tiles[channel].contains(this)) {
			tiles[channel].add(this);
		}
	}

	public void setChannel(int channel) {
		this.remove();
		this.channel = channel;
		this.add();
		this.update();
		this.recalculate();
	}

	private void updateOthersOfChannel() {
		ArrayList<TileEntityWirelessAnalog> li = tiles[channel];
		if (tiles[channel] == null || tiles[channel].isEmpty())
			return;
		for (int i = 0; i < li.size(); i++) {
			TileEntityWirelessAnalog te = li.get(i);
			if (te != this) {
				te.update();
				//ReikaJavaLibrary.pConsole(this+" >> "+te, Side.SERVER);
			}
		}
	}

	private static void recalculateChannel(int channel) {
		ArrayList<TileEntityWirelessAnalog> li = tiles[channel];
		if (li == null || li.isEmpty()) {
			channels[channel] = 0;
			return;
		}
		int min = 0;
		for (int i = 0; i < li.size(); i++) {
			TileEntityWirelessAnalog te = li.get(i);
			int lvl = te.getPowerInBack();
			if (lvl > min)
				min = lvl;
		}
		channels[channel] = min;
	}

	@Override
	public boolean canPowerSide(int s) {
		return s == this.getFacing().getOpposite().ordinal();
	}

	@Override
	public boolean isBinaryRedstone() {
		return false;
	}

	@Override
	public int getEmission() {
		return channels[channel];
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		channel = NBT.getInteger("chn");

		this.add();
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("chn", channel);
	}

	@Override
	public ArrayList<String> getMessages(World world, int x, int y, int z, int side) {
		ArrayList<String> li = new ArrayList();
		String s = String.format("Channel %d: Level %d from:", channel, channels[channel]);
		li.add(s);
		if (tiles[channel] != null) {
			for (int i = 0; i < tiles[channel].size(); i++) {
				li.add("  "+tiles[channel].get(i).toString());
			}
		}
		return li;
	}

}
