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
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.ItemBlocks.BlockRedTile;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CircuitBlockRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		ExpandedRedstoneTileEntity te = (ExpandedRedstoneTileEntity)world.getBlockTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		RedstoneTiles r = RedstoneTiles.TEList[meta];
		Icon[] ico = new Icon[6];
		Icon front = ((BlockRedTile)b).getFrontTexture(world, x, y, z);
		for (int i = 0; i < 6; i++)
			ico[i] = rb.getBlockIcon(b, world, x, y, z, i);
		Tessellator v5 = Tessellator.instance;

		double maxx = b.getBlockBoundsMaxX();
		double minx = b.getBlockBoundsMinX();
		double miny = b.getBlockBoundsMinY();
		double maxy = b.getBlockBoundsMaxY();
		double maxz = b.getBlockBoundsMaxZ();
		double minz = b.getBlockBoundsMinZ();

		float f3 = 0.5F;
		float f4 = 1.0F;
		float f5 = 0.8F;
		float f6 = 0.6F;
		float f7 = f4;
		float f8 = f4;
		float f9 = f4;
		float f10 = f3;
		float f11 = f5;
		float f12 = f6;
		float f13 = f3;
		float f14 = f5;
		float f15 = f6;
		float f16 = f3;
		float f17 = f5;
		float f18 = f6;

		f10 = f3;
		f11 = f5;
		f12 = f6;
		f13 = f3;
		f14 = f5;
		f15 = f6;
		f16 = f3;
		f17 = f5;
		f18 = f6;

		int l = b.getMixedBrightnessForBlock(world, x, y, z);

		v5.setBrightness(rb.renderMaxY < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y+1, z));
		v5.setColorOpaque_F(f7, f8, f9);

		v5.addTranslation(x, y, z);
		ForgeDirection dir = te.getFacing();
		if (RedstoneTiles.TEList[te.getTEIndex()].isReversedTopTexture())
			dir = dir.getOpposite();
		switch(dir) {
		case WEST:
			v5.addVertexWithUV(0, maxy, 0, ico[1].getMinU(), ico[1].getMaxV());
			v5.addVertexWithUV(0, maxy, 1, ico[1].getMaxU(), ico[1].getMaxV());
			v5.addVertexWithUV(1, maxy, 1, ico[1].getMaxU(), ico[1].getMinV());
			v5.addVertexWithUV(1, maxy, 0, ico[1].getMinU(), ico[1].getMinV());
			break;
		case NORTH:
			v5.addVertexWithUV(1, maxy, 0, ico[1].getMinU(), ico[1].getMaxV());
			v5.addVertexWithUV(0, maxy, 0, ico[1].getMaxU(), ico[1].getMaxV());
			v5.addVertexWithUV(0, maxy, 1, ico[1].getMaxU(), ico[1].getMinV());
			v5.addVertexWithUV(1, maxy, 1, ico[1].getMinU(), ico[1].getMinV());
			break;
		case SOUTH:
			v5.addVertexWithUV(0, maxy, 1, ico[1].getMinU(), ico[1].getMaxV());
			v5.addVertexWithUV(1, maxy, 1, ico[1].getMaxU(), ico[1].getMaxV());
			v5.addVertexWithUV(1, maxy, 0, ico[1].getMaxU(), ico[1].getMinV());
			v5.addVertexWithUV(0, maxy, 0, ico[1].getMinU(), ico[1].getMinV());
			break;
		case EAST:
			v5.addVertexWithUV(1, maxy, 1, ico[1].getMinU(), ico[1].getMaxV());
			v5.addVertexWithUV(1, maxy, 0, ico[1].getMaxU(), ico[1].getMaxV());
			v5.addVertexWithUV(0, maxy, 0, ico[1].getMaxU(), ico[1].getMinV());
			v5.addVertexWithUV(0, maxy, 1, ico[1].getMinU(), ico[1].getMinV());
			break;
		default:
			break;
		}
		v5.addTranslation(-x, -y, -z);

		v5.setBrightness(rb.renderMinY > 0.0D ? l : b.getMixedBrightnessForBlock(world, x, y-1, z));
		v5.setColorOpaque_F(f10, f13, f16);
		rb.renderFaceYNeg(b, x, y, z, ico[0]);

		v5.setBrightness(rb.renderMinZ > 0.0D ? l : b.getMixedBrightnessForBlock(world, x, y, z - 1));
		v5.setColorOpaque_F(f11, f14, f17);
		rb.renderFaceZNeg(b, x, y, z, ico[2]);

		v5.setBrightness(rb.renderMaxZ < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y, z + 1));
		v5.setColorOpaque_F(f11, f14, f17);
		rb.renderFaceZPos(b, x, y, z, ico[3]);

		v5.setBrightness(rb.renderMinX > 0.0D ? l : b.getMixedBrightnessForBlock(world, x - 1, y, z));
		v5.setColorOpaque_F(f12, f15, f18);
		rb.renderFaceXNeg(b, x, y, z, ico[4]);

		v5.setBrightness(rb.renderMaxX < 1.0D ? l : b.getMixedBrightnessForBlock(world, x + 1, y, z));
		v5.setColorOpaque_F(f12, f15, f18);
		rb.renderFaceXPos(b, x, y, z, ico[5]);
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return ClientProxy.tileRender;
	}

}
