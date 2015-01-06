/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone;

import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.event.world.WorldEvent;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.NEI_DragonAPI_Config;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ExpandedRedstone.Base.AnalogWireless;
import Reika.ExpandedRedstone.Registry.RedstoneBlocks;
import Reika.ExpandedRedstone.Registry.RedstoneItems;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import Reika.RotaryCraft.API.BlockColorInterface;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod( modid = "ExpandedRedstone", name="ExpandedRedstone", version="Gamma", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")


public class ExpandedRedstone extends DragonAPIMod {

	@Instance("ExpandedRedstone")
	public static ExpandedRedstone instance = new ExpandedRedstone();

	public static final String packetChannel = "ExpandedData";

	public static final ControlledConfig config = new ControlledConfig(instance, RedstoneOptions.optionList, null, 1);

	public static Block[] blocks = new Block[RedstoneBlocks.blockList.length];

	public static Item[] items = new Item[RedstoneItems.itemList.length];

	public static final TabRedstone tab = new TabRedstone(CreativeTabs.getNextID(), instance.getDisplayName());

	public static ModLogger logger;

	@SidedProxy(clientSide="Reika.ExpandedRedstone.ClientProxy", serverSide="Reika.ExpandedRedstone.CommonProxy")
	public static CommonProxy proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		this.verifyVersions();
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		logger = new ModLogger(instance, false);
		proxy.registerSounds();

		ReikaPacketHelper.registerPacketHandler(instance, packetChannel, new ExpandedPacketCore());

		this.addBlocks();
		this.addItems();

		this.basicSetup(evt);
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiLoader());
		proxy.registerRenderers();
		this.addRecipes();

		if (ModList.NEI.isLoaded())
			NEI_DragonAPI_Config.hideBlocks(blocks);
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		if (ModList.ROTARYCRAFT.isLoaded()) {
			for (int i = 0; i < RedstoneBlocks.blockList.length; i++) {
				RedstoneBlocks r = RedstoneBlocks.blockList[i];
				for (int k = 0; k < r.getNumberMetadatas(); k++)
					BlockColorInterface.addGPRBlockColor(r.getBlockInstance(), k, ReikaColorAPI.RGBtoHex(140, 140, 140));
			}
		}
	}

	@SubscribeEvent
	public void onClose(WorldEvent.Unload evt) {
		AnalogWireless.resetChannelData();
		logger.debug("Resetting wireless data.");

		//TileEntityEqualizer.unregisterAllInWorld(evt.world.provider.dimensionId);
	}

	private static void addItems() {
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, RedstoneItems.itemList, items);
	}

	private static void addBlocks() {
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, RedstoneBlocks.blockList, blocks);
		RedstoneBlocks.loadMappings();
		for (int i = 0; i < RedstoneTiles.TEList.length; i++)
			GameRegistry.registerTileEntity(RedstoneTiles.TEList[i].getTEClass(), "ExpRedstone"+RedstoneTiles.TEList[i].getName());
		RedstoneTiles.loadMappings();
	}

	private static void addRecipes() {
		RedstoneTiles.PLACER.addRecipe("CCC", "CDC", "CRC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'D', Blocks.dispenser);
		RedstoneTiles.CLOCK.addRecipe("srs", "rer", "srs", 's', ReikaItemHelper.stoneSlab, 'r', Items.redstone, 'e', Items.quartz);
		RedstoneTiles.BUD.addRecipe("ccc", "rne", "ccc", 'c', Blocks.cobblestone, 'r', Items.redstone, 'n', Items.quartz, 'e', Items.ender_pearl);
		RedstoneTiles.CAMOFLAGE.addSizedRecipe(8, "rir", "iei", "rir", 'r', Items.redstone, 'i', Items.iron_ingot, 'e', Items.ender_eye);
		RedstoneTiles.CHESTREADER.addRecipe(" c ", "rer", " s ", 's', ReikaItemHelper.stoneSlab, 'r', Items.redstone, 'e', Items.quartz, 'c', Blocks.trapped_chest);
		RedstoneTiles.DRIVER.addRecipe(" s ", "rer", " s ", 's', ReikaItemHelper.stoneSlab, 'r', Items.redstone, 'e', Items.quartz);
		RedstoneTiles.EFFECTOR.addRecipe("CCC", "NDE", "CRC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'D', Blocks.dispenser, 'N', Items.quartz, 'E', Items.ender_pearl);
		RedstoneTiles.PROXIMITY.addRecipe("rdr", "nen", "sss", 's', ReikaItemHelper.stoneSlab, 'r', Items.redstone, 'e', Items.ender_pearl, 'd', Items.gold_ingot, 'n', Items.quartz);
		RedstoneTiles.TOGGLE.addRecipe("tRt", "rer", "sRs", 't', Blocks.redstone_torch, 'R', Items.repeater, 's', ReikaItemHelper.stoneSlab, 'r', Items.redstone, 'e', Items.quartz);
		RedstoneTiles.WEATHER.addRecipe("nSn", "rnr", "sss", 's', ReikaItemHelper.stoneSlab, 'r', Items.redstone, 'n', Items.quartz, 'S', Blocks.daylight_detector);
		RedstoneTiles.PUMP.addRecipe("crc", "cCc", "cbc", 'c', Blocks.cobblestone, 'r', Items.redstone, 'C', Blocks.chest, 'b', Items.bucket);
		RedstoneTiles.HOPPER.addRecipe(" n ", "rtr", " r ", 't', RedstoneTiles.CLOCK.getItem(), 'r', Items.redstone, 'n', Items.quartz);

		RedstoneItems.BLUEWIRE.addSizedShapelessRecipe(2, ReikaItemHelper.lapisDye, Items.redstone);

		RedstoneTiles.BREAKER.addNBTRecipe(3, "CCC", "CPC", "CRC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'P', Items.diamond_pickaxe);
		RedstoneTiles.BREAKER.addNBTRecipe(2, "CCC", "CPC", "CRC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'P', Items.iron_pickaxe);
		RedstoneTiles.BREAKER.addNBTRecipe(1, "CCC", "CPC", "CRC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'P', Items.stone_pickaxe);
		RedstoneTiles.BREAKER.addNBTRecipe(0, "CCC", "CPC", "CRC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'P', Items.wooden_pickaxe);

		RedstoneTiles.EMITTER.addSizedRecipe(4, "CCC", "CRG", "CCC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'G', Blocks.glowstone);
		RedstoneTiles.RECEIVER.addSizedRecipe(4, "CCC", "GRC", "CCC", 'C', Blocks.cobblestone, 'R', Items.emerald, 'G', Blocks.glass);

		RedstoneTiles.SHOCK.addNBTRecipe(0, "CCC", "LPC", "CRC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'P', Items.ender_eye, 'L', Blocks.glass_pane); 	//range 1; damage 0.5
		RedstoneTiles.SHOCK.addNBTRecipe(1, "CCC", "LPC", "CRC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'P', Items.ender_eye, 'L', Items.quartz); //range 1; damage 1
		RedstoneTiles.SHOCK.addNBTRecipe(2, "CCC", "LPC", "CRC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'P', Items.ender_eye, 'L', Blocks.glowstone); 	//range 2; damage 0.5
		RedstoneTiles.SHOCK.addNBTRecipe(3, "CCC", "LPC", "CRC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'P', Items.ender_eye, 'L', Items.ender_pearl); 	//range 2; damage 1
		RedstoneTiles.SHOCK.addNBTRecipe(4, "CCC", "LPC", "CRC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'P', Items.ender_eye, 'L', Items.diamond); 		//range 2; damage 2
		RedstoneTiles.SHOCK.addNBTRecipe(5, "CCC", "LPC", "CRC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'P', Items.ender_eye, 'L', Items.emerald); 		//range 3; damage 2
		RedstoneTiles.SHOCK.addNBTRecipe(6, "CCC", "LPC", "CRC", 'C', Blocks.cobblestone, 'R', Items.redstone, 'P', Items.ender_eye, 'L', Items.nether_star);	//range 5; damage infinity

		RedstoneTiles.SCALER.addRecipe("rnr", "sss", 'r', Items.redstone, 'n', Items.quartz, 's', ReikaItemHelper.stoneSlab);
		RedstoneTiles.ANALOGTRANSMITTER.addSizedRecipe(2, "rrr", "nen", "sss", 'r', Items.redstone, 'n', Items.quartz, 's', ReikaItemHelper.stoneSlab, 'e', Items.ender_pearl);
		RedstoneTiles.ANALOGRECEIVER.addSizedRecipe(2, "rrr", "ene", "sss", 'r', Items.redstone, 'n', Items.quartz, 's', ReikaItemHelper.stoneSlab, 'e', Items.ender_pearl);
		RedstoneTiles.COLUMN.addRecipe("CCC", "RRR", "CRC", 'R', Items.redstone, 'C', Blocks.cobblestone);

		RedstoneTiles.COUNTDOWN.addRecipe("RQR", "QCQ", "RQR", 'R', Items.redstone, 'Q', Items.quartz, 'C', RedstoneTiles.CLOCK.getItem());
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
		return DragonAPICore.getReikaForumPage();
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}

	@Override
	public String getWiki() {
		return null;
	}

	@Override
	public String getUpdateCheckURL() {
		return CommandableUpdateChecker.reikaURL;
	}

}
