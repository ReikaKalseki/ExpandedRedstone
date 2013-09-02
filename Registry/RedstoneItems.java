/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.Registry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.IDRegistry;
import Reika.DragonAPI.Interfaces.RegistrationList;
import Reika.DragonAPI.Libraries.ReikaStringParser;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.ItemBlocks.ItemWirePlacer;
import cpw.mods.fml.common.registry.GameRegistry;

public enum RedstoneItems implements RegistrationList, IDRegistry {

	BLUEWIRE(false, "Lapis Wire", ItemWirePlacer.class);

	private boolean hasSubtypes;
	private String name;
	private Class itemClass;

	private RedstoneItems(boolean sub, String n, Class <?extends Item> iCl) {
		hasSubtypes = sub;
		name = n;
		itemClass = iCl;
	}

	public static final RedstoneItems[] itemList = RedstoneItems.values();


	public Class[] getConstructorParamTypes() {
		return new Class[]{int.class};
	}

	public Object[] getConstructorParams() {
		return new Object[]{ExpandedRedstone.config.getItemID(this.ordinal())};
	}

	public static boolean isRegistered(ItemStack is) {
		return isRegistered(is.itemID);
	}

	public static boolean isRegistered(int id) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].getShiftedID() == id)
				return true;
		}
		return false;
	}

	public static RedstoneItems getEntryByID(int id) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].getShiftedID() == id)
				return itemList[i];
		}
		throw new RegistrationException(ExpandedRedstone.instance, "Item ID "+id+" was called to the item registry but does not exist there!");
	}

	public static RedstoneItems getEntry(ItemStack is) {
		if (is == null)
			return null;
		return getEntryByID(is.itemID);
	}

	public String getName(int dmg) {
		if (this.hasMultiValuedName())
			return this.getMultiValuedName(dmg);
		return name;
	}

	public String getBasicName() {
		if (name.startsWith("#"))
			return name.substring(1);
		return name;
	}

	public String getMultiValuedName(int dmg) {
		if (!this.hasMultiValuedName())
			throw new RuntimeException("Item "+name+" was called for a multi-name, yet does not have one!");
		throw new RuntimeException("Item "+name+" was called for a multi-name, but it was not registered!");
	}

	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(name).toLowerCase();
	}
	/*
	public int getID() {
		return ExpandedRedstone.config.getItemID(this.ordinal());;
	}*/

	public int getShiftedID() {
		return ExpandedRedstone.config.getItemID(this.ordinal())+256;
	}

	public Item getItemInstance() {
		return ExpandedRedstone.items[this.ordinal()];
	}

	public boolean hasMultiValuedName() {
		return name.startsWith("#");
	}

	public boolean isCreativeOnly() {
		return false;
	}

	public int getNumberMetadatas() {
		if (!hasSubtypes)
			return 1;
		throw new RegistrationException(ExpandedRedstone.instance, "Item "+name+" has subtypes but the number was not specified!");
	}

	public ItemStack getCraftedProduct(int amt) {
		return new ItemStack(this.getShiftedID(), amt, 0);
	}

	public ItemStack getCraftedMetadataProduct(int amt, int meta) {
		return new ItemStack(this.getShiftedID(), amt, meta);
	}

	public ItemStack getStackOf() {
		return this.getCraftedProduct(1);
	}

	public ItemStack getStackOfMetadata(int meta) {
		return this.getCraftedMetadataProduct(1, meta);
	}

	public boolean overridesRightClick() {
		return false;
	}

	@Override
	public Class getObjectClass() {
		return itemClass;
	}

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return null;
	}

	@Override
	public boolean hasItemBlock() {
		return false;
	}

	@Override
	public String getConfigName() {
		return this.getBasicName();
	}

	@Override
	public int getDefaultID() {
		return 20500+this.ordinal();
	}

	@Override
	public boolean isBlock() {
		return false;
	}

	@Override
	public boolean isItem() {
		return true;
	}

	@Override
	public String getCategory() {
		return "Item IDs";
	}

	public boolean isDummiedOut() {
		return itemClass == null;
	}

	public void addRecipe(Object... params) {
		GameRegistry.addRecipe(this.getStackOf(), params);
	}

	public void addSizedRecipe(int num, Object... params) {
		GameRegistry.addRecipe(this.getCraftedProduct(num), params);
	}

	public void addMetaRecipe(int meta, Object... params) {
		GameRegistry.addRecipe(this.getStackOfMetadata(meta), params);
	}

	public void addSizedMetaRecipe(int meta, int num, Object... params) {
		GameRegistry.addRecipe(this.getCraftedMetadataProduct(num, meta), params);
	}

	public void addEnchantedRecipe(Enchantment e, int lvl, Object... params) {
		ItemStack is = this.getStackOf();
		is.addEnchantment(e, lvl);
		GameRegistry.addRecipe(is, params);
	}

	public void addShapelessRecipe(Object... params) {
		GameRegistry.addShapelessRecipe(this.getStackOf(), params);
	}

	public void addSizedShapelessRecipe(int amt, Object... params) {
		GameRegistry.addShapelessRecipe(this.getCraftedProduct(amt), params);
	}

	public void addRecipe(IRecipe ir) {
		GameRegistry.addRecipe(ir);
	}
}
