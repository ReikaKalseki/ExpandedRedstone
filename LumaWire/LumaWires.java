/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.LumaWire;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

import codechicken.lib.data.MCDataInput;
import codechicken.multipart.MultiPartRegistry.IPartFactory2;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mrtjp.projectred.transmission.WireDef.WireDef;
import scala.collection.mutable.WrappedArray;


public class LumaWires {


	public static final Registrar registry = new Registrar();
	public static final String BASE_NAME = "exr_glowire";

	static final HashMap<String, LumaWireEntry> names = new HashMap();
	static final EnumMap<ReikaDyeHelper, ImmutablePair<LumaWireEntry, LumaWireEntry>> colors = new EnumMap(ReikaDyeHelper.class);
	static final EnumMap<ReikaDyeHelper, WireDef> definitions = new EnumMap(ReikaDyeHelper.class);

	private static WireDef REDWIRE;
	//public static final WireDef DEFINITION = createDef();

	public static WireDef getRedWire() {
		return REDWIRE;
	}

	public static String[] createAndGetNames() {
		String[] arr = new String[32];
		for (int i = 0; i < 16; i++) {
			ReikaDyeHelper color = ReikaDyeHelper.dyes[i];
			LumaWireEntry e = new LumaWireEntry(color, false);
			LumaWireEntry e2 = new LumaWireEntry(color, true);
			arr[i] = e.getID();
			arr[i+16] = e2.getID();
			names.put(arr[i], e);
			names.put(arr[i+16], e2);
			colors.put(color, new ImmutablePair(e, e2));
			definitions.put(color, createDef(color));
		}
		return arr;
	}

	private static WireDef createDef(ReikaDyeHelper color) {
		try {
			Class c = Class.forName("mrtjp.projectred.transmission.WireDef$");
			Class c2 = Class.forName("mrtjp.projectred.transmission.WireDef$WireDef");

			Field instf = c.getDeclaredField("MODULE$");
			instf.setAccessible(true);
			Object inst = instf.get(null);

			//String wireType, String framedType, int thickness, int itemColour, Seq<String> textures
			Constructor con = c2.getDeclaredConstructor(String.class, String.class, int.class, int.class, scala.collection.Seq.class);
			con.setAccessible(true);

			if (REDWIRE == null) {
				Field f = c.getDeclaredField("RED_ALLOY");
				f.setAccessible(true);
				REDWIRE = (WireDef)f.get(inst);
			}

			//Field f2 = c2.getDeclaredField("mrtjp$projectred$transmission$WireDef$WireDef$$textures");
			//f2.setAccessible(true);
			//scala.collection.Seq<String> tex = (Seq)f2.get(REDWIRE);

			WrappedArray<String> newtex = scala.Predef.wrapRefArray(new String[]{"lumawire"}); //_"+color.name().toLowerCase(Locale.ENGLISH)
			ImmutablePair<LumaWireEntry, LumaWireEntry> pair = colors.get(color);

			WireDef ret = (WireDef)con.newInstance(pair.left.getID(), pair.right.getID(), 1, FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? color.getColor() : color.color, newtex);
			return ret;
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static WireDef getDefinition(ReikaDyeHelper color) {
		return definitions.get(color);
	}

	public static Collection<WireDef> getDefinitions() {
		return Collections.unmodifiableCollection(definitions.values());
	}

	public static ImmutablePair<LumaWireEntry, LumaWireEntry> getEntry(ReikaDyeHelper color) {
		return colors.get(color);
	}

	public static Collection<ImmutablePair<LumaWireEntry, LumaWireEntry>> getEntries() {
		return Collections.unmodifiableCollection(colors.values());
	}



	static class Registrar implements IPartFactory2 {

		@Override
		public TMultiPart createPart(String arg, NBTTagCompound arg1) {
			return this.create(arg);
		}

		@Override
		public TMultiPart createPart(String arg, MCDataInput arg1) {
			return this.create(arg);
		}

		private TMultiPart create(String arg) {
			return names.containsKey(arg) ? names.get(arg).createPart() : null;
		}

	}

	public static class LumaWireEntry {

		public final ReikaDyeHelper color;
		public final boolean isFramed;

		final TMultiPart cachedPart = this.createPart();

		private LumaWireEntry(ReikaDyeHelper dye, boolean fr) {
			color = dye;
			isFramed = fr;
		}

		public LumaWireEntry getCounterpart() {
			ImmutablePair<LumaWireEntry, LumaWireEntry> pair = colors.get(color);
			return isFramed ? pair.left : pair.right;
		}

		private TMultiPart createPart() {
			return isFramed ? new GlowingRedAlloyWireFramed(this) : new GlowingRedAlloyWire(this);
		}

		WireDef getDefinition() {
			return definitions.get(color);
		}

		public ItemStack getStack() {
			return isFramed ? this.getDefinition().makeFramedStack() : this.getDefinition().makeStack();
		}

		public ItemStack getStack(int amt) {
			return isFramed ? this.getDefinition().makeFramedStack(amt) : this.getDefinition().makeStack(amt);
		}

		public String getID() {
			return BASE_NAME+color.name().toLowerCase(Locale.ENGLISH)+(isFramed ? "_fr" : "");
		}

		public int getColor(int brightness) {
			return ReikaColorAPI.getColorWithBrightnessMultiplierRGBA(ReikaColorAPI.mixColors(color.getColor(), 0xffffff, 0.75F) << 8 | 0xff, 0.75F+0.25F*brightness/15F);
		}

		public int getLightValue(int brightness) {
			return ModList.COLORLIGHT.isLoaded() ? ReikaColorAPI.getPackedIntForColoredLight(this.getColor(brightness), brightness) : brightness;
		}

	}

}
