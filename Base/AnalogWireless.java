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
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Auxiliary.ModularLogger;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.TileEntities.TileEntityAnalogTransmitter;
import Reika.RotaryCraft.API.Interfaces.Transducerable;

public abstract class AnalogWireless extends TileRedstoneBase implements Transducerable {

	public static final int CHANNELS = 8192; //8192 channels

	protected static final String LOGGER_ID = "ExRWireless";

	private static final HashMap<UUID, int[]> channels = new HashMap();
	protected static final ArrayList<WorldLocation>[] transmitters = new ArrayList[CHANNELS];
	protected static final ArrayList<WorldLocation>[] receivers = new ArrayList[CHANNELS];

	protected int channel;

	static {
		ModularLogger.instance.addLogger(ExpandedRedstone.instance, LOGGER_ID);
	}

	private void registerUUID() {
		if (placerUUID != null) {
			if (!channels.containsKey(placerUUID)) {
				channels.put(placerUUID, new int[CHANNELS]);
			}
		}
	}

	protected int[] getChannels() {
		this.registerUUID();
		return channels.get(placerUUID);
	}

	public static void resetChannelData() {
		channels.clear();
		for (int i = 0; i < CHANNELS; i++) {
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

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.registerUUID();
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
		WorldLocation loc = new WorldLocation(this);
		ArrayList<WorldLocation>[] arr = this.getArray();
		if (arr[channel] != null)
			arr[channel].remove(loc);
	}

	public void add() {
		ArrayList<WorldLocation>[] arr = this.getArray();
		WorldLocation loc = new WorldLocation(this);
		if (arr[channel] == null) {
			arr[channel] = new ArrayList();
			arr[channel].add(loc);
		}
		else if (!arr[channel].contains(loc)) {
			arr[channel].add(loc);
		}
	}

	private ArrayList[] getArray() {
		return this instanceof TileEntityAnalogTransmitter ? transmitters : receivers;
	}

	@Override
	public final ArrayList<String> getMessages(World world, int x, int y, int z, int side) {
		ArrayList<String> li = new ArrayList();
		String s = String.format("Channel %d: Level %d from:", channel, this.getChannels()[channel]);
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
