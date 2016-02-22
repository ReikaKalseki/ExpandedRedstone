/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import java.util.TreeMap;

import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper.BiomeTemperatures;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;


public class TileEntityThermalMeter extends TileRedstoneBase {

	private static final TreeMap<Integer, Integer> temperatureMap = new TreeMap();

	private int emit;

	private final StepTimer calcTimer = new StepTimer(5).stagger();

	@Override
	public int getTEIndex() {
		return RedstoneTiles.THERMAL.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);

		if (calcTimer.checkCap()) {
			int lastEmit = emit;
			emit = this.calcEmission();
			if (emit != lastEmit) {
				this.update();
			}
		}
	}

	private int calcEmission() {
		int Tamb = ReikaWorldHelper.getAmbientTemperatureAt(worldObj, xCoord, yCoord, zCoord);
		int k1 = temperatureMap.floorKey(Tamb);
		int k2 = temperatureMap.ceilingKey(Tamb);
		return (int)Math.round(ReikaMathLibrary.linterpolate(Tamb, k1, k2, temperatureMap.get(k1), temperatureMap.get(k2)));
	}

	@Override
	public boolean isBinaryRedstone() {
		return false;
	}

	@Override
	public int getEmission() {
		return emit;
	}

	@Override
	public int getTopTexture() {
		return this.getEmission();
	}

	static {
		temperatureMap.put(Integer.MIN_VALUE, 0);
		temperatureMap.put(BiomeTemperatures.LUNAR.ambientTemperature, 1);
		temperatureMap.put(BiomeTemperatures.ICY.ambientTemperature, 2);
		temperatureMap.put(BiomeTemperatures.COOL.ambientTemperature, 4);
		temperatureMap.put(BiomeTemperatures.TEMPERATE.ambientTemperature, 6);
		temperatureMap.put(BiomeTemperatures.WARM.ambientTemperature, 8);
		temperatureMap.put(BiomeTemperatures.HOT.ambientTemperature, 10);
		temperatureMap.put(100, 14);
		temperatureMap.put(BiomeTemperatures.FIERY.ambientTemperature, 15);
	}

}
