/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.GUI;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.Base.AnalogWireless;

public class GuiAnalog extends GuiContainer {

	private static final ResourceLocation texture = new ResourceLocation("expandedredstone:textures/gui/analog.png");

	public AnalogWireless tile;
	private int channel;

	public GuiAnalog(EntityPlayer player, AnalogWireless te) {
		super(new CoreContainer(player, te));
		tile = te;
		channel = te.getChannel();
		ySize = 128;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int mid = j+xSize/2;

		int dy = 24;

		buttonList.add(new GuiButton(-1, 	mid-70, dy+k+0, 	50, 20, "-1"));
		buttonList.add(new GuiButton(-10, 	mid-70, dy+k+20, 	50, 20, "-10"));
		buttonList.add(new GuiButton(-100, 	mid-70, dy+k+40, 	50, 20, "-100"));
		buttonList.add(new GuiButton(-1000, mid-70, dy+k+60, 	50, 20, "-1000"));

		buttonList.add(new GuiButton(1, 	mid+20, dy+k, 		50, 20, "+1"));
		buttonList.add(new GuiButton(10, 	mid+20, dy+k+20, 	50, 20, "+10"));
		buttonList.add(new GuiButton(100, 	mid+20, dy+k+40, 	50, 20, "+100"));
		buttonList.add(new GuiButton(1000,	mid+20, dy+k+60, 	50, 20, "+1000"));
	}

	@Override
	public void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		channel += b.id;
		if (channel < 0)
			channel += AnalogWireless.CHANNELS;
		else if (channel >= AnalogWireless.CHANNELS)
			channel -= AnalogWireless.CHANNELS;

		this.initGui();

		ReikaPacketHelper.sendDataPacket(ExpandedRedstone.packetChannel, 0, tile, channel);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		String s = tile.getTEName();
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);

		String c = String.format("Channel: %d", channel);
		fontRenderer.drawString(c, xSize / 2 - fontRenderer.getStringWidth(c) / 2, 110, 0);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(texture);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}

}
