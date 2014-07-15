package Reika.ExpandedRedstone.TileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaFormatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

public class TileEntityCountdown extends ExpandedRedstoneTileEntity {

	private Delay time = Delay.FIVEMIN;
	private int count = time.length;

	private static enum Delay {
		QUARTERMIN(300, "15 Seconds"),
		HALFMIN(600, "30 Seconds"),
		MINUTE(1200, "1 Minute"),
		FIVEMIN(6000, "5 Minutes"),
		FIFTEEN(18000, "15 Minutes"),
		HALFHOUR(36000, "30 Minutes"),
		HOUR(72000, "1 Hour"),
		THREEHOUR(216000, "3 Hours"),
		SIXHOUR(432000, "6 Hours"),
		HALFDAY(864000, "12 Hours"),
		DAY(1728000, "24 Hours"),
		WEEK(12096000, "1 Week");

		public final int length;
		public final String name;

		private static final Delay[] list = values();

		private Delay(int l, String name) {
			length = l;
			this.name = name;
		}
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.COUNTDOWN.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (count > 0) {
			count--;
			this.setEmitting(false);
			if (RedstoneOptions.NOISES.getState()) {
				if (count%20 == 0) {
					ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.25F, 1F);
					world.markBlockForRenderUpdate(x, y, z);
				}
				else if (count%5 == 0) {
					ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.125F, 1.5F);
					world.markBlockForRenderUpdate(x, y, z);
				}
			}
		}
		else {
			if (!this.isEmitting())
				ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.75F, 0.5F);
			this.setEmitting(true);
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		count = NBT.getInteger("count");
		time = Delay.list[NBT.getInteger("delay")];
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("count", count);
		NBT.setInteger("delay", time.ordinal());
	}

	public void incrementDelay() {
		int ord = time.ordinal();
		if (ord == Delay.list.length-1) {
			ord = 0;
		}
		else {
			ord++;
		}
		time = Delay.list[ord];
		count = time.length;
		ReikaChatHelper.clearChat();
		ReikaChatHelper.write("Countdown set to "+time.name+" ("+time.length+" ticks)");
	}

	public void resetTimer() {
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.75F, 1F);
		count = time.length;
	}

	public int getTicksRemaining() {
		return count;
	}

	public int getTotalDelay() {
		return time.length;
	}

	public String getCountdownDisplay() {
		return ReikaFormatHelper.getTickAsHMS(count);
	}

	@Override
	public int getTopTexture() {
		if (this.isEmitting()) {
			return 10;
		}
		else {
			return 2+(count/5)%8;
		}
	}

}
