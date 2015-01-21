/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityArithmetic extends TileRedstoneBase {

	private Operators mode = Operators.ADD;

	public static enum Operators {
		ADD("+"),
		SUBTRACT("-"),
		MULTIPLY("*"),
		DIVIDE("/"),
		MODULUS("%"),
		POWER("^");

		public final String character;

		public static final Operators[] list = values();

		private Operators(String s) {
			character = s;
		}

		private int calculate(int n1, int n2) {
			switch(this) {
			case ADD:
				return n1+n2;
			case DIVIDE:
				return n2 > 0 ? n1/n2 : 15;
			case MODULUS:
				return n2 > 0 ? n1%n2 : 0;
			case MULTIPLY:
				return n1*n2;
			case POWER:
				return ReikaMathLibrary.intpow2(n1, n2);
			case SUBTRACT:
				return n1-n2;
			}
			return 0;
		}
	}

	public Operators getMode() {
		return mode;
	}

	public String getFunction() {
		int i1 = this.getInput1();
		int i2 = this.getInput2();
		return String.format("%d%s%d = %d -> %d", i1, mode.character, i2, mode.calculate(i1, i2), this.getOutput());
	}

	public void stepMode() {
		mode = Operators.list[(mode.ordinal()+1)%Operators.list.length];
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.8F);
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.ARITHMETIC.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
	}

	private int getOutput() {
		return (16+mode.calculate(this.getInput1(), this.getInput2())%16)%16; //clamp and wrap in 0-15
	}

	private int getInput1() {
		return this.getRedstoneLevelTo(ReikaDirectionHelper.getRightBy90(this.getFacing().getOpposite()));
	}

	private int getInput2() {
		return this.getRedstoneLevelTo(ReikaDirectionHelper.getLeftBy90(this.getFacing().getOpposite()));
	}

	@Override
	public boolean isBinaryRedstone() {
		return false;
	}

	@Override
	public boolean canPowerSide(int s) {
		return s == this.getFacing().getOpposite().ordinal();
	}

	@Override
	public int getEmission() {
		return this.getOutput();
	}

	@Override
	public int getTopTexture() {
		return mode.ordinal();
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		mode = Operators.list[NBT.getInteger("mode")];
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("mode", mode.ordinal());
	}

}
