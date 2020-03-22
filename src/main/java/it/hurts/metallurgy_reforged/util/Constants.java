/*
 * -------------------------------------------------------------------------------------------------------
 * Class: Constants
 * This class is part of Metallurgy 4 Reforged
 * Complete source code is available at: https://github.com/Davoleo/Metallurgy-4-Reforged
 * This code is licensed under GNU GPLv3
 * Authors: ItHurtsLikeHell & Davoleo
 * Copyright (c) 2020.
 * --------------------------------------------------------------------------------------------------------
 */

package it.hurts.metallurgy_reforged.util;

public class Constants {

	//Localized
	public static final String BITUMEN = Utils.localize("tooltip.metallurgy.bitumen");
	public static final String GAUNTLET_EFFECT_DISABLED = Utils.localize("tooltip.metallurgy.gauntlet_effect_disabled");
	public static final String POTASH_FERTILIZER = Utils.localize("tooltip.metallurgy.potash_fertilizer");
	public static final String PHOSPHORUS_LAMP = Utils.localize("tooltip.metallurgy.phosphorus_lamp");
	public static final String THERMITE_DUST = Utils.localize("tooltip.metallurgy.thermite");


	//Code constants
	//Tool Categories
	public static final String AXE = "axe";
	public static final String HOE = "hoe";
	public static final String PICKAXE = "pickaxe";
	public static final String SHOVEL = "shovel";
	public static final String SWORD = "sword";

	// Blast Resistance Constants
	//Disclaimer: These variables might need a balancement update
	//Davoleo isn't responsible for any blast resistance level complains
	public static final float LOW_TIER_BLAST_RESISTANCE = 6F;                            //or maybe 3, I don't remember
	public static final float MID_TIER_BLAST_RESISTANCE = 10F;                            //Cobblestone-like
	public static final float HIGH_TIER_BLAST_RESISTANCE = 15F;
	public static final float EXTREME_TIER_BLAST_RESISTANCE = 20F;                    //Obsidian-like
	public static final float UNBREAKABLE_TIER_BLAST_RESISTANCE = 18000000F;            //Bedrock-like

}
