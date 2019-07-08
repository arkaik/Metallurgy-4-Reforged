/*
 * -------------------------------------------------------------------------------------------------------
 * Class: CompatCrusher
 * This class is part of Metallurgy 4 Reforged
 * Complete source code is available at: https://github.com/Davoleo/Metallurgy-4-Reforged
 * This code is licensed under GNU GPLv3
 * Authors: ItHurtsLikeHell & Davoleo
 * Copyright (c) 2019.
 * --------------------------------------------------------------------------------------------------------
 */

package it.hurts.metallurgy_reforged.integration.mods.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import it.hurts.metallurgy_reforged.integration.mods.IntegrationCT;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Map;

import static it.hurts.metallurgy_reforged.recipe.BlockCrusherRecipes.getInstance;

@ZenClass("mods.metallurgyreforged.Crusher")
public class CompatCrusher {

    @ZenMethod
    public static void addRecipe(IIngredient input, IItemStack output)
    {
        CraftTweakerAPI.apply(new Add(input, output));
    }

    private static class Add implements IAction {

        private IIngredient input;
        private IItemStack output;

        Add(IIngredient input, IItemStack output)
        {
            this.input = input;
            this.output = output;
        }

        @Override
        public void apply()
        {
            ItemStack[] inputStacks = IntegrationCT.toStacks(input.getItemArray());
            ItemStack outputStack = IntegrationCT.toStack(output);

            for (ItemStack inputStack : inputStacks)
                getInstance().getRecipeMap().put(inputStack, outputStack);
        }

        @Override
        public String describe()
        {
            return "Adding Metallurgy-Reforged Crusher Recipe for " + output.getDisplayName();
        }
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output)
    {
        CraftTweakerAPI.apply(new Remove(output));
    }

    private static class Remove implements IAction {

        private IItemStack output;

        Remove(IItemStack output)
        {
            this.output = output;
        }

        @Override
        public void apply()
        {
            for (Map.Entry<ItemStack, ItemStack> entry : getInstance().getRecipeMap().entrySet())
            {
                if (entry.getValue() == IntegrationCT.toStack(output))
                {
                    getInstance().getRecipeMap().remove(entry.getKey());
                }
            }
        }

        @Override
        public String describe()
        {
            return "Removing Metallurgy-Reforged Crusher Recipe for " + output.getDisplayName();
        }
    }

}
