/*
 * -------------------------------------------------------------------------------------------------------
 * Class: ModFluids
 * This class is part of Metallurgy 4 Reforged
 * Complete source code is available at: https://github.com/Davoleo/Metallurgy-4-Reforged
 * This code is licensed under GNU GPLv3
 * Authors: ItHurtsLikeHell & Davoleo
 * Copyright (c) 2020.
 * --------------------------------------------------------------------------------------------------------
 */

package it.hurts.metallurgy_reforged.fluid;

import it.hurts.metallurgy_reforged.block.fluid.FluidBlockTar;
import it.hurts.metallurgy_reforged.material.ModMetals;
import it.hurts.metallurgy_reforged.util.BlockUtils;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.List;

public class ModFluids {

	//Molten FluidBlocks
	public static List<BlockFluidClassic> fluidBlocks = new ArrayList<>();

	//Fluids
	public static final FluidMolten TAR = ((FluidMolten) new FluidMolten("molten_tar", 0xFF111419, 1000).setLuminosity(0));
	public static final FluidMolten THERMITE = new FluidMolten("molten_thermite", 0xFFC44205, 3200);

	//FluidBlocks
	public static FluidBlockTar fluidBlockTar = new FluidBlockTar(TAR, Material.WATER);
	public static BlockFluidClassic fluidBlockThermite = new BlockFluidClassic(THERMITE, Material.LAVA);

	static
	{
		//Initialize Tar FluidBlock
		BlockUtils.initFluidBlock(fluidBlockThermite, "molten_thermite");
		BlockUtils.initFluidBlock(fluidBlockTar, "molten_tar");
	}

	public static void registerFluids()
	{
		ModMetals.metalMap.forEach((s, metal) -> {
			FluidRegistry.registerFluid(metal.getMolten());
			FluidRegistry.addBucketForFluid(metal.getMolten());
		});

		//Register Thermite and Tar
		FluidRegistry.registerFluid(TAR);
		FluidRegistry.registerFluid(THERMITE);

		//Add Tar and Thermite Buckets
		FluidRegistry.addBucketForFluid(TAR);
		FluidRegistry.addBucketForFluid(THERMITE);
	}

}
