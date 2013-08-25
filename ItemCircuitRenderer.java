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

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import Reika.ExpandedRedstone.Registry.RedstoneBlocks;

public class ItemCircuitRenderer implements IItemRenderer {

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
		float a = 0; float b = 0;
		if (type == ItemRenderType.INVENTORY) {
			RenderBlocks rb = new RenderBlocks();
			Minecraft.getMinecraft().renderEngine.bindTexture("/terrain.png");
			rb.renderBlockAsItem(Block.blocksList[RedstoneBlocks.TILEENTITY.getBlockID()], item.getItemDamage(), 1);
			//TileEntityRenderer.instance.renderTileEntityAt(RedstoneTiles.createTEFromMetadata(item.getItemDamage()), a, -0.1D, b, 0.0F);
		}
	}

}
