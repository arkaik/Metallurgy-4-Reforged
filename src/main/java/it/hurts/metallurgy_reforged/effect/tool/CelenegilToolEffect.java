/*==============================================================================
 = Class: CelenegilToolEffect
 = This class is part of Metallurgy 4: Reforged
 = Complete source code is available at https://github.com/Davoleo/Metallurgy-4-Reforged
 = This code is licensed under GNU GPLv3
 = Authors: Davoleo, ItHurtsLikeHell, PierKnight100
 = Copyright (c) 2018-2021.
 =============================================================================*/

package it.hurts.metallurgy_reforged.effect.tool;

import it.hurts.metallurgy_reforged.capabilities.effect.EffectDataProvider;
import it.hurts.metallurgy_reforged.capabilities.effect.ExtraFilledDataBundle;
import it.hurts.metallurgy_reforged.capabilities.effect.PlayerEffectData;
import it.hurts.metallurgy_reforged.capabilities.effect.ProgressiveDataBundle;
import it.hurts.metallurgy_reforged.effect.BaseMetallurgyEffect;
import it.hurts.metallurgy_reforged.effect.EnumEffectCategory;
import it.hurts.metallurgy_reforged.effect.IProgressiveEffect;
import it.hurts.metallurgy_reforged.material.ModMetals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

public class CelenegilToolEffect extends BaseMetallurgyEffect implements IProgressiveEffect {

    public CelenegilToolEffect()
    {
        super(ModMetals.CELENEGIL);
    }

    @Nonnull
    @Override
    public EnumEffectCategory getCategory()
    {
        return EnumEffectCategory.TOOL;
    }

    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event)
    {
        EntityPlayer player = event.getPlayer();

        if (!canBeApplied(player))
            return;

        PlayerEffectData data = player.getCapability(EffectDataProvider.PLAYER_EFFECT_DATA_CAPABILITY, null);
        NBTTagCompound compound = data.celenegilToolBundle.getExtra();
        int brokenBlocks = compound.getInteger("broken_blocks");

        //If escalation is complete
        if (brokenBlocks >= 5)
        {
            if (brokenBlocks >= 60)
                player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 30, 2));
            else if (brokenBlocks >= 15)
                player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 30, 1));
            else
                player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 30, 0));


            //System.out.println("unbreakable: " + brokenBlocks);
            //Cancel Item Damage
            ItemStack tool = player.getHeldItemMainhand();
            tool.setItemDamage(tool.getItemDamage() - 1);
            //Reset the effect as active
            compound.setInteger("prev_broken_blocks", brokenBlocks);
        }
        else
        {
            //No effects at this stage yet
            //System.out.println("Increasing...");
            event.getWorld().playSound(null, player.getPosition(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.75F, 0.75F + ((brokenBlocks + 1) / 5F));
        }
        //reset inactive state
        compound.setBoolean("inactive", false);
        //Increase number of broken blocks
        compound.setInteger("broken_blocks", brokenBlocks + 1);
        data.celenegilToolBundle.setExtra(compound);
    }

    @Override
    public void onStep(World world, EntityPlayer player, int maxSteps, int step, ProgressiveDataBundle bundle)
    {
        NBTTagCompound data = ((ExtraFilledDataBundle<NBTTagCompound>) bundle).getExtra();
        boolean inactive = data.getBoolean("inactive");

        //on step one the effect is flagged as inactive (this flag is removed if the player mines another block)
        if (step == 1)
        {
            //System.out.println("inactive");
            data.setBoolean("inactive", true);
        }

        //if the effect is inactive after one second it means the effect has to be reset
        if (step == 3 && inactive)
        {
            //System.out.println("Reset");
            data.setInteger("broken_blocks", 0);
            player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 0.75F, 0.75F);
        }
    }
}
