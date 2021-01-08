/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone;

import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.DragonAPI.Instantiable.IO.SoundLoader;
import Reika.DragonAPI.Instantiable.Rendering.CustomWireRenderer;
import Reika.ExpandedRedstone.Registry.RedstoneItems;
import Reika.ExpandedRedstone.Registry.RedstoneSounds;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	private static CircuitBlockRenderer circuit;
	private static CustomWireRenderer wire;

	@Override
	public void registerSounds() {
		//RotarySounds.addSounds();
		//MinecraftForge.EVENT_BUS.register(new SoundLoader(RotaryCraft.instance, SoundRegistry.soundList));
		//MinecraftForge.EVENT_BUS.register(new SoundLoader(ExpandedRedstone.instance, SoundRegistry.soundList, SoundRegistry.SOUND_FOLDER));

		//CustomSoundHandler.instance.addSound("ExpandedRedstone", "shock", SoundCategory.BLOCKS);

		sounds.register();
	}

	@Override
	public void registerRenderers() {
		tileRender = RenderingRegistry.getNextAvailableRenderId();
		wireRender = RenderingRegistry.getNextAvailableRenderId();
		circuit = new CircuitBlockRenderer(tileRender);
		wire = new CustomWireRenderer(wireRender);
		RenderingRegistry.registerBlockHandler(tileRender, circuit);
		RenderingRegistry.registerBlockHandler(wireRender, wire);

		MinecraftForgeClient.registerItemRenderer(RedstoneItems.PLACER.getItemInstance(), new CircuitPlacerRenderer());
	}

	// Override any other methods that need to be handled differently client side.

	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}

	public static CircuitBlockRenderer getCircuit() {
		return circuit;
	}

}
