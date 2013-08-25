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
import net.minecraftforge.client.MinecraftForgeClient;
import Reika.ExpandedRedstone.Registry.RedstoneItems;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	public static final int tileRender = RenderingRegistry.getNextAvailableRenderId();
	public static final CircuitBlockRenderer block = new CircuitBlockRenderer();
	public static final ItemCircuitRenderer item = new ItemCircuitRenderer();

	@Override
	public void registerSounds() {
		//RotarySounds.addSounds();
		//MinecraftForge.EVENT_BUS.register(new SoundLoader(RotaryCraft.instance, SoundRegistry.soundList));
	}

	@Override
	public void registerRenderers() {
		this.loadModels();

		this.registerSpriteSheets();
		this.registerBlockSheets();
	}

	public void loadModels() {
		MinecraftForgeClient.registerItemRenderer(RedstoneItems.PLACER.getShiftedID(), new ItemCircuitRenderer());
	}


	private void registerBlockSheets() {
		RenderingRegistry.registerBlockHandler(tileRender, block);
	}

	private void registerSpriteSheets() {

	}

	// Override any other methods that need to be handled differently client side.

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
