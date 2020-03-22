/*
 * -------------------------------------------------------------------------------------------------------
 * Class: MetalStats
 * This class is part of Metallurgy 4 Reforged
 * Complete source code is available at: https://github.com/Davoleo/Metallurgy-4-Reforged
 * This code is licensed under GNU GPLv3
 * Authors: ItHurtsLikeHell & Davoleo
 * Copyright (c) 2020.
 * --------------------------------------------------------------------------------------------------------
 */

package it.hurts.metallurgy_reforged.material;

import it.hurts.metallurgy_reforged.Metallurgy;
import it.hurts.metallurgy_reforged.block.BlockOreDict;
import it.hurts.metallurgy_reforged.fluid.FluidMolten;
import it.hurts.metallurgy_reforged.item.ItemOreDict;
import it.hurts.metallurgy_reforged.item.armor.ItemArmorBase;
import it.hurts.metallurgy_reforged.item.tool.*;
import it.hurts.metallurgy_reforged.util.Constants;
import it.hurts.metallurgy_reforged.util.MetallurgyTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.util.EnumHelper;

public class MetalStats {

	private final String name, oreDictName;
	private final int blockHarvest;
	private final float blockBlastResistance;
	public static float blockResistance;

	//Ore Properties
	int minVeinSize, maxVeinSize;
	int chance;
	int minY, maxY;

	//Color
	private int color;

	private final int oreHarvest;

	private final ArmorStats armor;
	private final ToolStats tool;
	private final FluidStats fluid;

	private ItemTool.ToolMaterial toolMaterial;
	private ItemArmor.ArmorMaterial armorMaterial;

	public Metal createMetal()
	{
		//name should be in format [snake_case], oreName should be in format [camelCase]
		ItemOreDict dust = new ItemOreDict(name + "_dust", "dust" + oreDictName, MetallurgyTabs.tabDust, null).setCreativeTab(MetallurgyTabs.tabDust);
		ItemOreDict ingot = new ItemOreDict(name + "_ingot", "ingot" + oreDictName, MetallurgyTabs.tabIngot, null).setCreativeTab(MetallurgyTabs.tabIngot);
		ItemOreDict nugget = new ItemOreDict(name + "_nugget", "nugget" + oreDictName, MetallurgyTabs.tabNugget, null).setCreativeTab(MetallurgyTabs.tabNugget);
		BlockOreDict block = new BlockOreDict(name + "_block", "block" + oreDictName, false, "pickaxe", blockHarvest, blockBlastResistance, MetallurgyTabs.tabBlock);
		BlockOreDict ore = null;
		if (oreHarvest >= 0)
		{
			ore = new BlockOreDict(name + "_ore", "ore" + oreDictName, false, "pickaxe", oreHarvest, blockBlastResistance, MetallurgyTabs.tabOre);
		}

		FluidMolten molten = fluid.func.apply(new FluidMolten(name, fluid.still, fluid.flowing, fluid.mapColor, fluid.TEMPERATURE != 0 ? fluid.TEMPERATURE : automaticTemperature()));

		ItemArmorBase[] armorPieces = null;
		Item[] tools = null;

		if (armor != null)
		{
			ItemArmor.ArmorMaterial armorMaterial = createArmorMaterial();
			ItemArmorBase helmet = new ItemArmorBase(armorMaterial, EntityEquipmentSlot.HEAD, name + "_helmet");
			ItemArmorBase chestplate = new ItemArmorBase(armorMaterial, EntityEquipmentSlot.CHEST, name + "_chestplate");
			ItemArmorBase leggings = new ItemArmorBase(armorMaterial, EntityEquipmentSlot.LEGS, name + "_leggings");
			ItemArmorBase boots = new ItemArmorBase(armorMaterial, EntityEquipmentSlot.FEET, name + "_boots");

			armorPieces = new ItemArmorBase[]{helmet, chestplate, leggings, boots};
		}

		if (tool != null)
		{
			Item.ToolMaterial toolMaterial = createToolMaterial();
			ItemAxeBase axe = new ItemAxeBase(toolMaterial, name + "_axe");
			ItemHoeBase hoe = new ItemHoeBase(toolMaterial, name + "_hoe");
			ItemPickaxeBase pickaxe = new ItemPickaxeBase(toolMaterial, name + "_pickaxe");
			ItemShovelBase shovel = new ItemShovelBase(toolMaterial, name + "_shovel");
			ItemSwordBase sword = new ItemSwordBase(toolMaterial, name + "_sword");

			tools = new Item[]{axe, hoe, pickaxe, shovel, sword};
		}

		return new Metal(this, ingot, dust, nugget, ore, block, molten, tools, armorPieces);
	}

	private int automaticTemperature()
	{
		float output = 1000F;
		if (blockBlastResistance == Constants.LOW_TIER_BLAST_RESISTANCE)
		{
			output = blockBlastResistance * 60f;
		}
		else if (blockBlastResistance != Constants.UNBREAKABLE_TIER_BLAST_RESISTANCE)
		{
			output = blockBlastResistance * 36F;
		}
		return Math.round(output);
	}

	/**
	 * @param name            name of the metal - all lowercase with underlines separating words (ex: dark_steel)
	 * @param oreDictName     ore dict name of the metal - CamelCase with no underscores or spaces (DarkSteel)
	 * @param blockHarvest    the harvest level of the metal block
	 * @param blastResistance the resistance to explosions of the metal block
	 * @param armor           the ArmorStats instance representing this metal's stats, or null if there is no armor
	 * @param tool            the ToolStats instance representing this metal's stats, or null if there are no tools
	 * @param fluid           the ToolStats instance representing this metal's stats, or null if there are no tools
	 * @param oreHarvest      the harvest level of the metal ore or -1 if no ore should be generated
	 */
	public MetalStats(String name, String oreDictName, int blockHarvest, float blastResistance, ArmorStats armor, ToolStats tool, FluidStats fluid, int oreHarvest)
	{
		this.name = name;
		this.oreDictName = oreDictName;
		this.blockHarvest = blockHarvest;
		this.blockBlastResistance = blastResistance;
		this.armor = armor;
		this.tool = tool;
		this.fluid = fluid;
		this.oreHarvest = oreHarvest;
	}

	public String getName()
	{
		return name;
	}

	public String getOreDictName()
	{
		return oreDictName;
	}

	public ArmorStats getArmorStats()
	{
		return armor;
	}

	public ToolStats getToolStats()
	{
		return tool;
	}

	public ItemTool.ToolMaterial getToolMaterial()
	{
		return toolMaterial;
	}

	public ItemArmor.ArmorMaterial getArmorMaterial()
	{
		return armorMaterial;
	}

	public int getMetalColor()
	{
		return fluid.mapColor;
	}

	private ItemArmor.ArmorMaterial createArmorMaterial()
	{
		if (armor == null)
			throw new UnsupportedOperationException("No Armor Stats Loaded");

		return armorMaterial = EnumHelper.addArmorMaterial(this.getName(), Metallurgy.MODID + ":" + this.getName(), armor.getDurability(), armor.getDamageReduction(), armor.getArmorMagic(), armor.getEquipSound(), armor.getToughness());
	}

	private Item.ToolMaterial createToolMaterial()
	{
		if (tool == null)
			throw new UnsupportedOperationException("No Tool Stats Loaded");

		return toolMaterial = EnumHelper.addToolMaterial(this.getName(), tool.getHarvestLevel(), tool.getMaxUses(), tool.getEfficiency(), tool.getDamage(), tool.getToolMagic());
	}
}
