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

import net.minecraft.world.World;
import Reika.DragonAPI.Auxiliary.CustomSoundHandler;
import Reika.DragonAPI.Instantiable.CustomWireRenderer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	public static final CircuitBlockRenderer circuit = new CircuitBlockRenderer();
	public static final CustomWireRenderer wire = new CustomWireRenderer(wireRender);

	@Override
	public void registerSounds() {
		//RotarySounds.addSounds();
		//MinecraftForge.EVENT_BUS.register(new SoundLoader(RotaryCraft.instance, SoundRegistry.soundList));
		//MinecraftForge.EVENT_BUS.register(new SoundLoader(ExpandedRedstone.instance, SoundRegistry.soundList, SoundRegistry.SOUND_FOLDER));

		CustomSoundHandler.instance.addSound("ExpandedRedstone", "shock");
	}

	@Override
	public void registerRenderers() {
		tileRender = RenderingRegistry.getNextAvailableRenderId();
		wireRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(tileRender, circuit);
		RenderingRegistry.registerBlockHandler(wireRender, wire);
	}

	// Override any other methods that need to be handled differently client side.

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
