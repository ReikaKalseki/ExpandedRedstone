/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.Registry;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.TileEntities.TileEntity555;
import Reika.ExpandedRedstone.TileEntities.TileEntityBUD;
import Reika.ExpandedRedstone.TileEntities.TileEntityBreaker;
import Reika.ExpandedRedstone.TileEntities.TileEntityChestReader;
import Reika.ExpandedRedstone.TileEntities.TileEntityDriver;
import Reika.ExpandedRedstone.TileEntities.TileEntityEffector;
import Reika.ExpandedRedstone.TileEntities.TileEntityPlacer;
import Reika.ExpandedRedstone.TileEntities.TileEntityProximity;
import Reika.ExpandedRedstone.TileEntities.TileEntityToggle;
import Reika.ExpandedRedstone.TileEntities.TileEntityWeather;

public enum RedstoneTiles {

	BUD("Block Update Detector", TileEntityBUD.class),
	BREAKER("Block Breaker", TileEntityBreaker.class),
	PLACER("Block Placer", TileEntityPlacer.class),
	EFFECTOR("Item Effector", TileEntityEffector.class),
	PROXIMITY("Proximity Detector", TileEntityProximity.class),
	TOGGLE("Toggle Latch", TileEntityToggle.class),
	WEATHER("Weather Sensor", TileEntityWeather.class),
	CHESTREADER("Chest Reader", TileEntityChestReader.class),
	DRIVER("Signal Driver", TileEntityDriver.class),
	CLOCK("Redstone Clock", TileEntity555.class);

	private Class te;
	private String name;

	public static final RedstoneTiles[] TEList = RedstoneTiles.values();

	private RedstoneTiles(String n, Class<? extends TileEntity> cl) {
		te = cl;
		name = n;
	}

	public static TileEntity createTEFromMetadata(int meta) {
		Class TEClass = TEList[meta].te;
		try {
			return (TileEntity)TEClass.newInstance();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new RegistrationException(ExpandedRedstone.instance, "Metadata "+meta+" failed to instantiate its TileEntity of "+TEClass);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RegistrationException(ExpandedRedstone.instance, "Metadata "+meta+" failed illegally accessed its TileEntity of "+TEClass);
		}
	}

	public Class<? extends TileEntity> getTEClass() {
		return te;
	}

	public String getName() {
		return name;
	}

}
