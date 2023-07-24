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

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import Reika.RotaryCraft.API.Event.NoteEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TileEntityEqualizer extends TileRedstoneBase {

	private int pitch;
	private int redstone;

	private static final ArrayList<TileEntityEqualizer> eqs = new ArrayList();

	public static void unregisterAllInWorld(int dim) {
		Iterator<TileEntityEqualizer> it = eqs.iterator();
		while (it.hasNext()) {
			TileEntityEqualizer eq = it.next();
			if (eq.isInWorld() && eq.worldObj.provider.dimensionId == dim) {
				MinecraftForge.EVENT_BUS.unregister(eq);
				eqs.remove(eq);
			}
		}
	}

	private void register() {
		if (!eqs.contains(this)) {
			MinecraftForge.EVENT_BUS.register(this);
			eqs.add(this);
		}
	}

	@SubscribeEvent
	public void onMusicBoxNote(NoteEvent e) {
		int dp = Math.abs(e.notePitch-pitch);
		if (dp == 0)
			redstone = Math.max(redstone, 15);
		else if (dp == 1)
			redstone = Math.max(redstone, 4);
		else if (dp == 2)
			redstone = Math.max(redstone, 1);
		ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public RedstoneTiles getTile() {
		return RedstoneTiles.EQUALIZER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (redstone > 0) {
			redstone--;
			ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.register();
	}

	@Override
	public boolean isBinaryRedstone() {
		return false;
	}

	@Override
	public int getEmission() {
		return redstone;
	}

	public void incrementValue() {
		if (pitch < 63)
			pitch++;
		else
			pitch = 0;
		//ReikaChatHelper.clearChat();
		ReikaChatHelper.write("Set to note "+pitch);
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.8F);
	}

	public void decrementValue() {
		if (pitch > 0)
			pitch--;
		//ReikaChatHelper.clearChat();
		ReikaChatHelper.write("Set to note "+pitch);
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.8F);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		pitch = NBT.getInteger("chn");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("chn", pitch);
	}

	public int getPitch() {
		return pitch;
	}

}
