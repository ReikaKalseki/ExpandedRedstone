package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityRedstoneRelay extends TileRedstoneBase {

	private boolean mirror;

	@Override
	public int getTEIndex() {
		return RedstoneTiles.RELAY.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

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
		return this.isActive() ? this.getInput() : 0;
	}

	private int getInput() {
		return this.getPowerInBack();
	}

	private boolean isActive() {
		ForgeDirection dir = mirror ? ReikaDirectionHelper.getRightBy90(this.getFacing()) : ReikaDirectionHelper.getLeftBy90(this.getFacing());
		return this.getRedstoneLevelTo(dir) > 0;
	}

	@Override
	public int getTopTexture() {
		return mirror ? 1 : 0;
	}

	public void toggle() {
		mirror = !mirror;
		this.update();
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.8F);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		mirror = NBT.getBoolean("mirror");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setBoolean("mirror", mirror);
	}

}
