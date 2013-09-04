/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.ItemBlocks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Interfaces.WireBlock;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.Registry.RedstoneBlocks;
import Reika.ExpandedRedstone.Registry.RedstoneItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockExpandedWire extends Block implements WireBlock {

	private Icon[] icons = new Icon[2];

	public BlockExpandedWire(int ID, Material mat) {
		super(ID, mat);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
	}


	@Override
	public final ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		int id = this.idPicked(world, x, y, z);
		if (id == 0)
			return null;
		int meta = world.getBlockMetadata(target.blockX, target.blockY, target.blockZ);
		return RedstoneItems.BLUEWIRE.getStackOfMetadata(meta);
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();
		li.add(RedstoneItems.BLUEWIRE.getStackOfMetadata(meta));
		return li;
	}

	/**
	 * When false, power transmission methods do not look at other redstone wires. Used internally during
	 * updateCurrentStrength.
	 */
	private boolean wiresProvidePower = true;
	private Set blocksNeedingUpdate = new HashSet();

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
	 * cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int par2, int par3, int par4)
	{
		return null;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	/**
	 * The type of render function that is called for this block
	 */
	@Override
	public int getRenderType()
	{
		return ExpandedRedstone.proxy.wireRender;
	}

	@Override
	@SideOnly(Side.CLIENT)

	/**
	 * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
	 * when first determining what to render.
	 */
	public int colorMultiplier(IBlockAccess iba, int par2, int par3, int par4)
	{
		return this.getColor().darker().darker().getRGB();
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceBlockAt(World world, int par2, int par3, int par4)
	{
		return world.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) || world.getBlockId(par2, par3 - 1, par4) == Block.glowStone.blockID;
	}

	/**
	 * Sets the strength of the wire current (0-15) for this block based on neighboring blocks and propagates to
	 * neighboring redstone wires
	 */
	private void updateAndPropagateCurrentStrength(World world, int par2, int par3, int par4)
	{
		this.calculateCurrentChanges(world, par2, par3, par4, par2, par3, par4);
		ArrayList arraylist = new ArrayList(blocksNeedingUpdate);
		blocksNeedingUpdate.clear();

		for (int l = 0; l < arraylist.size(); ++l)
		{
			ChunkPosition chunkposition = (ChunkPosition)arraylist.get(l);
			world.notifyBlocksOfNeighborChange(chunkposition.x, chunkposition.y, chunkposition.z, blockID);
		}
	}

	public int getStrongestIndirectPower(World world, int par1, int par2, int par3)
	{
		int l = 0;

		for (int i1 = 0; i1 < 6; ++i1)
		{
			int id = world.getBlockId(par1 + Facing.offsetsXForSide[i1], par2 + Facing.offsetsYForSide[i1], par3 + Facing.offsetsZForSide[i1]);
			if (id != Block.redstoneWire.blockID) {
				int j1 = world.getIndirectPowerLevelTo(par1 + Facing.offsetsXForSide[i1], par2 + Facing.offsetsYForSide[i1], par3 + Facing.offsetsZForSide[i1], i1);

				if (j1 >= 15)
				{
					return 15;
				}

				if (j1 > l)
				{
					l = j1;
				}
			}
		}

		return l;
	}

	private void calculateCurrentChanges(World world, int par2, int par3, int par4, int par5, int par6, int par7)
	{
		int k1 = world.getBlockMetadata(par2, par3, par4);
		byte b0 = 0;
		int l1 = this.getMaxCurrentStrength(world, par5, par6, par7, b0);
		wiresProvidePower = false;
		int i2 = this.getStrongestIndirectPower(world, par2, par3, par4);
		wiresProvidePower = true;

		if (i2 > 0 && i2 > l1 - 1)
		{
			l1 = i2;
		}

		int j2 = 0;

		for (int k2 = 0; k2 < 4; ++k2)
		{
			int l2 = par2;
			int i3 = par4;

			if (k2 == 0)
			{
				l2 = par2 - 1;
			}

			if (k2 == 1)
			{
				++l2;
			}

			if (k2 == 2)
			{
				i3 = par4 - 1;
			}

			if (k2 == 3)
			{
				++i3;
			}

			if (l2 != par5 || i3 != par7)
			{
				j2 = this.getMaxCurrentStrength(world, l2, par3, i3, j2);
			}

			if (world.isBlockNormalCube(l2, par3, i3) && !world.isBlockNormalCube(par2, par3 + 1, par4))
			{
				if ((l2 != par5 || i3 != par7) && par3 >= par6)
				{
					j2 = this.getMaxCurrentStrength(world, l2, par3 + 1, i3, j2);
				}
			}
			else if (!world.isBlockNormalCube(l2, par3, i3) && (l2 != par5 || i3 != par7) && par3 <= par6)
			{
				j2 = this.getMaxCurrentStrength(world, l2, par3 - 1, i3, j2);
			}
		}

		if (j2 > l1)
		{
			l1 = j2 - 1;
		}
		else if (l1 > 0)
		{
			--l1;
		}
		else
		{
			l1 = 0;
		}

		if (i2 > l1 - 1)
		{
			l1 = i2;
		}

		if (k1 != l1)
		{
			world.setBlockMetadataWithNotify(par2, par3, par4, l1, 2);
			blocksNeedingUpdate.add(new ChunkPosition(par2, par3, par4));
			blocksNeedingUpdate.add(new ChunkPosition(par2 - 1, par3, par4));
			blocksNeedingUpdate.add(new ChunkPosition(par2 + 1, par3, par4));
			blocksNeedingUpdate.add(new ChunkPosition(par2, par3 - 1, par4));
			blocksNeedingUpdate.add(new ChunkPosition(par2, par3 + 1, par4));
			blocksNeedingUpdate.add(new ChunkPosition(par2, par3, par4 - 1));
			blocksNeedingUpdate.add(new ChunkPosition(par2, par3, par4 + 1));
		}
	}

	/**
	 * Calls World.notifyBlocksOfNeighborChange() for all neighboring blocks, but only if the given block is a redstone
	 * wire.
	 */
	private void notifyWireNeighborsOfNeighborChange(World world, int x, int y, int z)
	{
		int id = world.getBlockId(x, y, z);
		if (id == blockID)
		{
			world.notifyBlocksOfNeighborChange(x, y, z, blockID);
			world.notifyBlocksOfNeighborChange(x - 1, y, z, blockID);
			world.notifyBlocksOfNeighborChange(x + 1, y, z, blockID);
			world.notifyBlocksOfNeighborChange(x, y, z - 1, blockID);
			world.notifyBlocksOfNeighborChange(x, y, z + 1, blockID);
			world.notifyBlocksOfNeighborChange(x, y - 1, z, blockID);
			world.notifyBlocksOfNeighborChange(x, y + 1, z, blockID);
		}
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, int par2, int par3, int par4)
	{
		super.onBlockAdded(world, par2, par3, par4);

		if (!world.isRemote)
		{
			this.updateAndPropagateCurrentStrength(world, par2, par3, par4);
			world.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, blockID);
			world.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, blockID);
			this.notifyWireNeighborsOfNeighborChange(world, par2 - 1, par3, par4);
			this.notifyWireNeighborsOfNeighborChange(world, par2 + 1, par3, par4);
			this.notifyWireNeighborsOfNeighborChange(world, par2, par3, par4 - 1);
			this.notifyWireNeighborsOfNeighborChange(world, par2, par3, par4 + 1);

			if (world.isBlockNormalCube(par2 - 1, par3, par4))
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2 - 1, par3 + 1, par4);
			}
			else
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2 - 1, par3 - 1, par4);
			}

			if (world.isBlockNormalCube(par2 + 1, par3, par4))
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2 + 1, par3 + 1, par4);
			}
			else
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2 + 1, par3 - 1, par4);
			}

			if (world.isBlockNormalCube(par2, par3, par4 - 1))
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2, par3 + 1, par4 - 1);
			}
			else
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2, par3 - 1, par4 - 1);
			}

			if (world.isBlockNormalCube(par2, par3, par4 + 1))
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2, par3 + 1, par4 + 1);
			}
			else
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2, par3 - 1, par4 + 1);
			}
		}
	}

	/**
	 * ejects contained items into the world, and notifies neighbours of an update, as appropriate
	 */
	@Override
	public void breakBlock(World world, int par2, int par3, int par4, int par5, int par6)
	{
		super.breakBlock(world, par2, par3, par4, par5, par6);

		if (!world.isRemote)
		{
			world.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, blockID);
			world.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, blockID);
			world.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, blockID);
			world.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, blockID);
			world.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, blockID);
			world.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, blockID);
			this.updateAndPropagateCurrentStrength(world, par2, par3, par4);
			this.notifyWireNeighborsOfNeighborChange(world, par2 - 1, par3, par4);
			this.notifyWireNeighborsOfNeighborChange(world, par2 + 1, par3, par4);
			this.notifyWireNeighborsOfNeighborChange(world, par2, par3, par4 - 1);
			this.notifyWireNeighborsOfNeighborChange(world, par2, par3, par4 + 1);

			if (world.isBlockNormalCube(par2 - 1, par3, par4))
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2 - 1, par3 + 1, par4);
			}
			else
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2 - 1, par3 - 1, par4);
			}

			if (world.isBlockNormalCube(par2 + 1, par3, par4))
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2 + 1, par3 + 1, par4);
			}
			else
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2 + 1, par3 - 1, par4);
			}

			if (world.isBlockNormalCube(par2, par3, par4 - 1))
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2, par3 + 1, par4 - 1);
			}
			else
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2, par3 - 1, par4 - 1);
			}

			if (world.isBlockNormalCube(par2, par3, par4 + 1))
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2, par3 + 1, par4 + 1);
			}
			else
			{
				this.notifyWireNeighborsOfNeighborChange(world, par2, par3 - 1, par4 + 1);
			}
		}
	}

	/**
	 * Returns the current strength at the specified block if it is greater than the passed value, or the passed value
	 * otherwise. Signature: (world, x, y, z, strength)
	 */
	private int getMaxCurrentStrength(World world, int par2, int par3, int par4, int par5)
	{
		//ReikaJavaLibrary.pConsole(world.getBlockId(par2, par3, par4));
		if (world.getBlockId(par2, par3, par4) != blockID)
		{
			return par5;
		}
		else
		{
			int i1 = world.getBlockMetadata(par2, par3, par4);
			return i1 > par5 ? i1 : par5;
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World world, int par2, int par3, int par4, int par5)
	{
		if (!world.isRemote)
		{
			boolean flag = this.canPlaceBlockAt(world, par2, par3, par4);

			if (flag)
			{
				this.updateAndPropagateCurrentStrength(world, par2, par3, par4);
			}
			else
			{
				this.dropBlockAsItem(world, par2, par3, par4, 0, 0);
				world.setBlockToAir(par2, par3, par4);
			}

			super.onNeighborBlockChange(world, par2, par3, par4, par5);
		}
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int isProvidingStrongPower(IBlockAccess iba, int par2, int par3, int par4, int par5)
	{
		return !wiresProvidePower ? 0 : this.isProvidingWeakPower(iba, par2, par3, par4, par5);
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int isProvidingWeakPower(IBlockAccess iba, int x, int y, int z, int s)
	{
		if (!wiresProvidePower)
		{
			return 0;
		}
		else
		{
			int i1 = iba.getBlockMetadata(x, y, z);

			if (i1 == 0)
			{
				return 0;
			}
			else if (s == 1)
			{
				return i1;
			}
			else
			{
				boolean flag = iba.getBlockId(x-1, y, z) == Block.redstoneWire.blockID || isPoweredOrRepeater(iba, x - 1, y, z, 1) || !iba.isBlockNormalCube(x - 1, y, z) && isPoweredOrRepeater(iba, x - 1, y - 1, z, -1);
				boolean flag1 = iba.getBlockId(x+1, y, z) == Block.redstoneWire.blockID || isPoweredOrRepeater(iba, x + 1, y, z, 3) || !iba.isBlockNormalCube(x + 1, y, z) && isPoweredOrRepeater(iba, x + 1, y - 1, z, -1);
				boolean flag2 = iba.getBlockId(x, y, z-1) == Block.redstoneWire.blockID || isPoweredOrRepeater(iba, x, y, z - 1, 2) || !iba.isBlockNormalCube(x, y, z - 1) && isPoweredOrRepeater(iba, x, y - 1, z - 1, -1);
				boolean flag3 = iba.getBlockId(x, y, z+1) == Block.redstoneWire.blockID || isPoweredOrRepeater(iba, x, y, z + 1, 0) || !iba.isBlockNormalCube(x, y, z + 1) && isPoweredOrRepeater(iba, x, y - 1, z + 1, -1);

				if (!iba.isBlockNormalCube(x, y + 1, z))
				{
					if (iba.isBlockNormalCube(x - 1, y, z) && isPoweredOrRepeater(iba, x - 1, y + 1, z, -1))
					{
						flag = true;
					}

					if (iba.isBlockNormalCube(x + 1, y, z) && isPoweredOrRepeater(iba, x + 1, y + 1, z, -1))
					{
						flag1 = true;
					}

					if (iba.isBlockNormalCube(x, y, z - 1) && isPoweredOrRepeater(iba, x, y + 1, z - 1, -1))
					{
						flag2 = true;
					}

					if (iba.isBlockNormalCube(x, y, z + 1) && isPoweredOrRepeater(iba, x, y + 1, z + 1, -1))
					{
						flag3 = true;
					}
				}

				//ReikaJavaLibrary.pConsole(flag+":"+flag1+":"+flag2+":"+flag3);

				if (this.isConnectedTo(iba, x, y, z, ForgeDirection.values()[s].getOpposite().ordinal()))
					return i1;
				return !flag2 && !flag1 && !flag && !flag3 && s >= 2 && s <= 5 ? i1 : (s == 2 && flag2 && !flag && !flag1 ? i1 : (s == 3 && flag3 && !flag && !flag1 ? i1 : (s == 4 && flag && !flag2 && !flag3 ? i1 : (s == 5 && flag1 && !flag2 && !flag3 ? i1 : 0))));
			}
		}
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean canProvidePower()
	{
		return wiresProvidePower;
	}

	/**
	 * Returns true if redstone wire can connect to the specified block. Params: World, X, Y, Z, side (not a normal
	 * notch-side, this can be 0, 1, 2, 3 or -1)
	 */
	public static boolean isPowerProviderOrWire(IBlockAccess iba, int x, int y, int z, int s)
	{
		return iba.getBlockId(x, y, z) == getBlockInstance().blockID;
	}

	@Override
	@SideOnly(Side.CLIENT)

	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	public void randomDisplayTick(World world, int par2, int par3, int par4, Random par5Random)
	{
		int l = world.getBlockMetadata(par2, par3, par4);

		if (l > 0)
		{
			double d0 = par2 + 0.5D + (par5Random.nextFloat() - 0.5D) * 0.2D;
			double d1 = par3 + 0.0625F;
			double d2 = par4 + 0.5D + (par5Random.nextFloat() - 0.5D) * 0.2D;
			float f = l / 15.0F;
			float f1 = f * 0.6F + 0.4F;

			if (l == 0)
			{
				f1 = 0.0F;
			}

			float f2 = f * f * 0.7F - 0.5F;
			float f3 = f * f * 0.6F - 0.7F;

			if (f2 < 0.0F)
			{
				f2 = 0.0F;
			}

			if (f3 < 0.0F)
			{
				f3 = 0.0F;
			}

			world.spawnParticle("reddust", d0, d1, d2, (0.5F+this.getPowerState(world, par2, par3, par4)/8F)*this.getColor().getRed()/256F, (0.5F+this.getPowerState(world, par2, par3, par4)/8F)*this.getColor().getGreen()/256F, (0.5F+this.getPowerState(world, par2, par3, par4)/8F)*this.getColor().getBlue()/256F);
		}
	}

	/**
	 * Returns true if the block coordinate passed can provide power, or is a redstone wire, or if its a repeater that
	 * is powered.
	 */
	public static boolean isPoweredOrRepeater(IBlockAccess par0IBlockAccess, int par1, int par2, int par3, int par4)
	{
		boolean flag = false;
		if (isPowerProviderOrWire(par0IBlockAccess, par1, par2, par3, par4))
		{
			flag = true;
		}
		else
		{
			int i1 = par0IBlockAccess.getBlockId(par1, par2, par3);

			if (i1 == Block.redstoneRepeaterActive.blockID)
			{
				int j1 = par0IBlockAccess.getBlockMetadata(par1, par2, par3);
				flag = par4 == (j1 & 3);
			}
			else
			{
				flag = false;
			}
		}
		return flag;
	}

	@Override
	@SideOnly(Side.CLIENT)

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	public int idPicked(World world, int par2, int par3, int par4)
	{
		return RedstoneItems.BLUEWIRE.getShiftedID();
	}

	@Override
	@SideOnly(Side.CLIENT)

	/**
	 * When this method is called, your block should register all the icons it needs with the given IconRegister. This
	 * is the only chance you get to register icons.
	 */
	public void registerIcons(IconRegister ico)
	{
		icons[0] = ico.registerIcon("ExpandedRedstone:wire_core");
		icons[1] = ico.registerIcon("ExpandedRedstone:wire_side");
	}

	public static BlockExpandedWire getBlockInstance() {
		return ((BlockExpandedWire)ExpandedRedstone.blocks[RedstoneBlocks.WIRE.ordinal()]);
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
	{
		return false;//world.getBlockId(x, y, z) == blockID;//Block.blocksList[blockID].canProvidePower() && side != -1;
	}

	@Override
	public int getPowerState(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean isConnectedTo(IBlockAccess world, int x, int y, int z, int s) {
		ForgeDirection dir = ForgeDirection.values()[s];
		int id = world.getBlockId(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
		int meta = world.getBlockMetadata(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
		int idup = world.getBlockId(x+dir.offsetX, y+dir.offsetY+1, z+dir.offsetZ);
		int iddown = world.getBlockId(x+dir.offsetX, y+dir.offsetY-1, z+dir.offsetZ);
		if (id == blockID)
			return true;
		if (idup == blockID)
			return true;
		if (iddown == blockID && !Block.opaqueCubeLookup[id])
			return true;
		if (id == 0)
			return false;
		if (id == Block.redstoneWire.blockID)
			return false;

		//Fully functional but not present in vanilla redstone
		//if (id == Block.pistonBase.blockID || id == Block.pistonStickyBase.blockID || id == Block.pistonMoving.blockID)
		//	return true;

		Block b = Block.blocksList[id];
		if (b instanceof BlockDirectional) {
			int direct = BlockDirectional.getDirection(meta);
			return (Direction.offsetX[direct] == -dir.offsetX && Direction.offsetZ[direct] == -dir.offsetZ) || (Direction.offsetX[direct] == dir.offsetX && Direction.offsetZ[direct] == dir.offsetZ);
		}
		if (b.canConnectRedstone(world, x, y, z, dir.getOpposite().ordinal()))
			return true;
		return false;
	}

	public boolean drawWireUp(IBlockAccess world, int x, int y, int z, int s) {
		ForgeDirection dir = ForgeDirection.values()[s];
		int id = world.getBlockId(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
		int idup = world.getBlockId(x+dir.offsetX, y+dir.offsetY+1, z+dir.offsetZ);
		int iddown = world.getBlockId(x+dir.offsetX, y+dir.offsetY-1, z+dir.offsetZ);
		if (id == blockID)
			return false;
		if (idup == blockID)
			return true;
		return false;
	}

	@Override
	public Color getColor() {
		return new Color(40, 80, 255);
	}

	@Override
	public Icon getConnectedSideOverlay() {
		return icons[1];
	}

	@Override
	public Icon getBaseTexture() {
		return icons[0];
	}

	@Override
	public Icon getIcon(int side, int meta) {
		return this.getBaseTexture();
	}

	@Override
	public int getRenderColor(int par1)
	{
		return 0xffffffff;
	}

}
