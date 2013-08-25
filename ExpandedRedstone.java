/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone;

import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.ControlledConfig;
import Reika.DragonAPI.Instantiable.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.ExpandedRedstone.Registry.RedstoneBlocks;
import Reika.ExpandedRedstone.Registry.RedstoneItems;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod( modid = "ExpandedRedstone", name="ExpandedRedstone", version="beta", certificateFingerprint = "@GET_FINGERPRINT@")
@NetworkMod(clientSideRequired = true, serverSideRequired = true/*,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { "RealBiomesData" }, packetHandler = ClientPackets.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = { "RealBiomesData" }, packetHandler = ServerPackets.class)*/)

public class ExpandedRedstone extends DragonAPIMod {

	@Instance("ExpandedRedstone")
	public static ExpandedRedstone instance = new ExpandedRedstone();

	public static final ControlledConfig config = new ControlledConfig(instance, RedstoneOptions.optionList, RedstoneBlocks.blockList, RedstoneItems.itemList, null, 1);

	public static Block[] blocks = new Block[RedstoneBlocks.blockList.length];

	public static Item[] items = new Item[RedstoneItems.itemList.length];

	public static final TabRedstone tab = new TabRedstone(CreativeTabs.getNextID(), instance.getDisplayName());

	public static ModLogger logger;

	@SidedProxy(clientSide="Reika.ExpandedRedstone.ClientProxy", serverSide="Reika.ExpandedRedstone.CommonProxy")
	public static CommonProxy proxy;

	@Override
	@PreInit
	public void preload(FMLPreInitializationEvent evt) {
		config.initProps(evt);
		logger = new ModLogger(instance, RedstoneOptions.LOGLOADING.getState(), RedstoneOptions.DEBUGMODE.getState(), false);
		proxy.registerSounds();
	}

	@Override
	@Init
	public void load(FMLInitializationEvent event) {
		this.addBlocks();
		this.addItems();
		NetworkRegistry.instance().registerGuiHandler(instance, new GuiLoader());
		proxy.registerRenderers();
	}

	@Override
	@PostInit
	public void postload(FMLPostInitializationEvent evt) {

	}

	private static void addItems() {
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, RedstoneItems.itemList, items, logger.shouldLog());
	}

	private static void addBlocks() {
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, RedstoneBlocks.blockList, blocks, logger.shouldLog());
		for (int i = 0; i < RedstoneTiles.TEList.length; i++)
			GameRegistry.registerTileEntity(RedstoneTiles.TEList[i].getTEClass(), "ExpRedstone"+RedstoneTiles.TEList[i].getName());
	}

	@Override
	public String getDisplayName() {
		return "Expanded Redstone";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return null;
	}

	@Override
	public boolean hasWiki() {
		return false;
	}

	@Override
	public URL getWiki() {
		return null;
	}

	@Override
	public boolean hasVersion() {
		return false;
	}

	@Override
	public String getVersionName() {
		return null;
	}

}
