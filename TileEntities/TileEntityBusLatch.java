package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.ExpandedRedstone.Base.TileRedstoneBase;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

import mrtjp.projectred.api.IBundledTile;
import mrtjp.projectred.api.ProjectRedAPI;

@Strippable(value={"mrtjp.projectred.api.IBundledTile"})
public class TileEntityBusLatch extends TileRedstoneBase implements IBundledTile {

	private int lastSignal;
	private int ticksSinceChange;
	private boolean singleChannel;

	@Override
	public byte[] getBundledSignal(int dir) {
		int signal = lastSignal;
		if (dir != this.getFacing().ordinal())
			signal = 0;
		else if (singleChannel && ticksSinceChange < 2)
			signal = 0;
		return this.unpackSignal(signal);
	}

	private byte[] unpackSignal(int signal) {
		byte[] ret = new byte[16];
		for (int i = 0; i < 16; i++) {
			int bit = 1 << i;
			boolean has = (signal & bit) != 0;
			ret[i] = (byte)(has ? 255 : 0);
		}
		return ret;
	}

	private int packSignal(byte[] signal) {
		int ret = 0;
		for (int i = 0; i < 16; i++) {
			int bit = 1 << i;
			boolean has = signal[i] != 0;
			ret |= has ? bit : 0;
		}
		return ret;
	}

	@Override
	public boolean canConnectBundled(int side) {
		return side == this.getFacing().ordinal() || side == this.getFacing().getOpposite().ordinal();
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.BUSLATCH.ordinal();
	}

	public void toggle(EntityPlayer ep) {
		singleChannel = !singleChannel;
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.5F, 0.5F);
		ReikaChatHelper.sendChatToPlayer(ep, "Latch is now "+(singleChannel ? "Single" : "Multiple")+"-Channel.");
		this.triggerBlockUpdate();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		ticksSinceChange++;
		if (singleChannel && ticksSinceChange == 2)
			this.update();
		ForgeDirection dir = this.getFacing();
		if (dir == ForgeDirection.UNKNOWN)
			return;
		ForgeDirection dir2 = ReikaDirectionHelper.getLeftBy90(dir);
		boolean reset = this.getRedstoneLevelTo(dir2) > 0 || this.getRedstoneLevelTo(dir2.getOpposite()) > 0;
		byte[] in = ProjectRedAPI.transmissionAPI.getBundledInput(world, x, y, z, dir.getOpposite().ordinal());
		int signal = reset || in == null ? 0 : this.packSignal(in);
		if (lastSignal != signal && (reset || signal != 0)) {
			ticksSinceChange = 0;
			lastSignal = signal;
			this.update();
		}
	}

	@Override
	public int getTopTexture() {
		return singleChannel ? super.getTopTexture()+1 : super.getTopTexture();
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		singleChannel = NBT.getBoolean("single");
		lastSignal = NBT.getInteger("signal");
		ticksSinceChange = NBT.getInteger("cticks");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("signal", lastSignal);
		NBT.setBoolean("single", singleChannel);
		NBT.setInteger("cticks", ticksSinceChange);
	}

}
