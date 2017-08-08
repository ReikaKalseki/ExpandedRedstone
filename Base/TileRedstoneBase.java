/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.Base;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public abstract class TileRedstoneBase extends TileEntityBase {

	private ForgeDirection facing;

	private boolean emit;

	private StepTimer pulsar = new StepTimer(0);
	private boolean isPulsing = false;

	public abstract int getTEIndex();

	public void updateEntity(World world, int x, int y, int z) {
		if (isPulsing) {
			pulsar.update();
			if (pulsar.checkCap())
				this.setEmitting(false);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.update();
	}

	@Override
	public String getTEName() {
		return RedstoneTiles.TEList[this.getTEIndex()].getName();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {}

	@Override
	public final Block getTileEntityBlockID() {
		return RedstoneTiles.TEList[this.getTEIndex()].getBlock();
	}

	public final boolean isEmitting() {
		return emit;
	}

	public final int getPowerInBack() {
		int x = this.getBackX();
		int y = this.getBackY();
		int z = this.getBackZ();
		int lvl = worldObj.getIndirectPowerLevelTo(x, y, z, this.getFacing().ordinal());
		Block id = worldObj.getBlock(x, y, z);
		int meta = worldObj.getBlockMetadata(x, y, z);
		return lvl >= 15 ? lvl : Math.max(lvl, id == Blocks.redstone_wire ? meta : 0);
	}

	protected void sendPulse(int length) {
		pulsar = new StepTimer(length);
		isPulsing = true;
		emit = true;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
	}

	public ForgeDirection getFacing() {
		if (facing == null)
			return ForgeDirection.UNKNOWN;
		return facing;
	}

	public void setFacing(ForgeDirection dir) {
		facing = dir;
	}

	public int getFacingX() {
		if (facing == null)
			return Integer.MIN_VALUE;
		return xCoord+facing.offsetX;
	}

	public int getFacingY() {
		if (facing == null)
			return Integer.MIN_VALUE;
		return yCoord+facing.offsetY;
	}

	public int getFacingZ() {
		if (facing == null)
			return Integer.MIN_VALUE;
		return zCoord+facing.offsetZ;
	}

	public int getFacingXScaled(int d) {
		if (facing == null)
			return Integer.MIN_VALUE;
		return xCoord+facing.offsetX*d;
	}

	public int getFacingYScaled(int d) {
		if (facing == null)
			return Integer.MIN_VALUE;
		return yCoord+facing.offsetY*d;
	}

	public int getFacingZScaled(int d) {
		if (facing == null)
			return Integer.MIN_VALUE;
		return zCoord+facing.offsetZ*d;
	}

	public int getBackX() {
		if (facing == null)
			return Integer.MIN_VALUE;
		return xCoord-facing.offsetX;
	}

	public int getBackY() {
		if (facing == null)
			return Integer.MIN_VALUE;
		return yCoord-facing.offsetY;
	}

	public int getBackZ() {
		if (facing == null)
			return Integer.MIN_VALUE;
		return zCoord-facing.offsetZ;
	}

	public boolean isBinaryRedstone() {
		return true;
	}

	public int getEmission() {
		return 0;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		facing = dirs[NBT.getInteger("face")];

		emit = NBT.getBoolean("emitting");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		if (facing != null)
			NBT.setInteger("face", facing.ordinal());

		NBT.setBoolean("emitting", emit);
	}

	public int getTextureForSide(int s) {
		return 0;
	}

	public boolean isOverridingIcon(int side) {
		return false;
	}

	public IIcon getOverridingIcon(int side) {
		return null;
	}

	public boolean canPowerSide(int s) {
		return true;
	}

	public int getTopTexture() {
		return this.getFacing() == ForgeDirection.UP ? this.getFrontTexture() : 0;
	}

	public int getFrontTexture() {
		return 0;
	}

	public int[] getTopTextures() {
		return new int[]{this.getTopTexture()};
	}

	public final int getRedstoneLevelTo(ForgeDirection dir) {
		Block id = worldObj.getBlock(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
		int meta = worldObj.getBlockMetadata(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
		int lvl = worldObj.getIndirectPowerLevelTo(xCoord, yCoord, zCoord, dir.ordinal());
		return lvl >= 15 ? lvl : Math.max(lvl, id == Blocks.redstone_wire ? meta : 0);
	}

	public void rotate() {
		RedstoneTiles r = RedstoneTiles.TEList[this.getTEIndex()];
		if (r.canBeVertical()) {
			int o = facing.ordinal();
			o++;
			if (o >= dirs.length-1)
				o = 0;
			this.setFacing(dirs[o]);
		}
		else {
			int o = facing.ordinal();
			o++;
			if (o >= dirs.length-1)
				o = 2;
			this.setFacing(dirs[o]);
		}
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 2F);
	}

	public boolean canProvideStrongPower() {
		return true;
	}

	protected void setEmitting(boolean e) {
		if (emit != e) {
			emit = e;
			this.update();
		}
	}

	protected void toggleEmitting() {
		this.update();
		emit = !emit;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0;
	}

	protected void update() {
		ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public final int getRedstoneOverride() {
		return 0;
	}
}
