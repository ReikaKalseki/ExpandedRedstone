/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.Registry;

import java.util.Locale;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.Registry.ItemEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.ExpandedRedstone.ExpandedRedstone;
import Reika.ExpandedRedstone.ItemBlocks.ItemCircuitPlacer;
import Reika.ExpandedRedstone.ItemBlocks.ItemWirePlacer;

import cpw.mods.fml.common.registry.GameRegistry;

public enum RedstoneItems implements ItemEnum {

	BLUEWIRE(false, "Lapis Wire", ItemWirePlacer.class),
	PLACER(true, "#Placer", ItemCircuitPlacer.class);

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
		return new Class[]{};
	}

	public Object[] getConstructorParams() {
		return new Object[]{};
	}

	public static boolean isRegistered(ItemStack is) {
		return isRegistered(is.getItem());
	}

	public static boolean isRegistered(Item id) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].getItemInstance() == id)
				return true;
		}
		return false;
	}

	public static RedstoneItems getEntryByID(Item id) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].getItemInstance() == id)
				return itemList[i];
		}
		throw new RegistrationException(ExpandedRedstone.instance, "Item ID "+id+" was called to the item registry but does not exist there!");
	}

	public static RedstoneItems getEntry(ItemStack is) {
		if (is == null)
			return null;
		return getEntryByID(is.getItem());
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
		if (this == PLACER)
			return RedstoneTiles.TEList[dmg].getName();
		throw new RuntimeException("Item "+name+" was called for a multi-name, but it was not registered!");
	}

	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(name).toLowerCase(Locale.ENGLISH);
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
		if (this == PLACER)
			return RedstoneTiles.TEList.length;
		throw new RegistrationException(ExpandedRedstone.instance, "Item "+name+" has subtypes but the number was not specified!");
	}

	public ItemStack getCraftedProduct(int amt) {
		return new ItemStack(this.getItemInstance(), amt, 0);
	}

	public ItemStack getCraftedMetadataProduct(int amt, int meta) {
		return new ItemStack(this.getItemInstance(), amt, meta);
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

	@Override
	public boolean overwritingItem() {
		return false;
	}
}
