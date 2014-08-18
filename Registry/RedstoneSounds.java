/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.Registry;

import Reika.DragonAPI.Instantiable.WorldLocation;
import Reika.DragonAPI.Interfaces.SoundEnum;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ExpandedRedstone.ExpandedRedstone;

import java.net.URL;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public enum RedstoneSounds implements SoundEnum {

	SHOCK("shock", SoundCategory.BLOCKS);

	public static final RedstoneSounds[] soundList = values();

	public static final String PREFIX = "Reika/ExpandedRedstone/";
	public static final String SOUND_FOLDER = "Sounds/";
	private static final String SOUND_PREFIX = "Reika.ExpandedRedstone.Sounds.";
	private static final String SOUND_DIR = "Sounds/";
	private static final String SOUND_EXT = ".ogg";
	private static final String MUSIC_FOLDER = "music/";
	private static final String MUSIC_PREFIX = "music.";

	private final String path;
	private final String name;
	private final SoundCategory category;

	private boolean isVolumed = false;

	private RedstoneSounds(String n, SoundCategory cat) {
		if (n.startsWith("#")) {
			isVolumed = true;
			n = n.substring(1);
		}
		name = n;
		path = SOUND_FOLDER+name+SOUND_EXT;
		category = cat;
	}

	public void playSound(World world, double x, double y, double z, float vol, float pitch) {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
			return;
		;//ReikaPacketHelper.sendSoundPacket(ExpandedRedstone.packetChannel, this, world, x, y, z, vol, pitch);
	}

	public void playSoundAtBlock(World world, int x, int y, int z, float vol, float pitch) {
		this.playSound(world, x+0.5, y+0.5, z+0.5, vol, pitch);
	}

	public void playSoundAtBlock(World world, int x, int y, int z) {
		this.playSound(world, x+0.5, y+0.5, z+0.5, 1, 1);
	}

	public void playSoundAtBlock(TileEntity te) {
		this.playSoundAtBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
	}

	public void playSoundAtBlock(WorldLocation loc) {
		this.playSoundAtBlock(loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord);
	}

	public String getName() {
		return this.name();
	}

	public String getPath() {
		return path;
	}

	public URL getURL() {
		return ExpandedRedstone.class.getResource(SOUND_DIR+name+SOUND_EXT);
	}

	public static RedstoneSounds getSoundByName(String name) {
		for (int i = 0; i < soundList.length; i++) {
			if (soundList[i].name().equals(name))
				return soundList[i];
		}
		ReikaJavaLibrary.pConsole("\""+name+"\" does not correspond to a registered sound!");
		return null;
	}

	@Override
	public SoundCategory getCategory() {
		return category;
	}
}
