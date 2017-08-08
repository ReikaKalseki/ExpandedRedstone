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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class CircuitPlacerRenderer implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		RenderBlocks rb = (RenderBlocks)data[0];
		RedstoneTiles r = RedstoneTiles.TEList[item.getItemDamage()];
		Block b = r.getBlock();
		int meta = r.getBlockMetadata();
		ReikaTextureHelper.bindTerrainTexture();
		if (type == ItemRenderType.ENTITY)
			GL11.glScaled(0.5, 0.5, 0.5);
		ClientProxy.circuit.renderInventoryBlock(b, meta, 0, rb);
		if (type == ItemRenderType.ENTITY)
			GL11.glScaled(2, 2, 2);
	}

}
