/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.Base;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ExpandedRedstone.TileEntities.TileEntityAnalogReceiver;
import Reika.ExpandedRedstone.TileEntities.TileEntityAnalogTransmitter;
import Reika.RotaryCraft.API.Interfaces.Transducerable;

public abstract class AnalogWireless extends TileRedstoneBase implements Transducerable {

	public static final int CHANNELS = 8192;

	protected static final int[] channels = new int[CHANNELS]; //8192 channels
	protected static final ArrayList<TileEntityAnalogTransmitter>[] transmitters = new ArrayList[CHANNELS];
	protected static final ArrayList<TileEntityAnalogReceiver>[] receivers = new ArrayList[CHANNELS];

	protected int channel;

	public static void resetChannelData() {
		for (int i = 0; i < channels.length; i++) {
			channels[i] = 0;
			transmitters[i] = null;
			receivers[i] = null;
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);

		ArrayList[] arr = this.getArray();
		if (arr[channel] == null || !arr[channel].contains(this))
			if (!world.isRemote)
				this.add();
	}

	public final int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.remove();
		this.channel = channel;
		this.add();
		this.update();
	}

	public void remove() {
		ArrayList[] arr = this.getArray();
		if (arr[channel] != null)
			arr[channel].remove(this);
	}

	public void add() {
		ArrayList[] arr = this.getArray();
		if (arr[channel] == null) {
			arr[channel] = new ArrayList();
			arr[channel].add(this);
		}
		else if (!arr[channel].contains(this)) {
			arr[channel].add(this);
		}
	}

	private ArrayList[] getArray() {
		return this instanceof TileEntityAnalogTransmitter ? transmitters : receivers;
	}

	@Override
	public final ArrayList<String> getMessages(World world, int x, int y, int z, int side) {
		ArrayList<String> li = new ArrayList();
		String s = String.format("Channel %d: Level %d from:", channel, channels[channel]);
		li.add(s);
		if (transmitters[channel] != null) {
			for (int i = 0; i < transmitters[channel].size(); i++) {
				li.add("  "+transmitters[channel].get(i).toString());
			}
		}
		li.add("To:");
		if (receivers[channel] != null) {
			for (int i = 0; i < receivers[channel].size(); i++) {
				li.add("  "+receivers[channel].get(i).toString());
			}
		}
		return li;
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		channel = NBT.getInteger("chn");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("chn", channel);
	}

}
