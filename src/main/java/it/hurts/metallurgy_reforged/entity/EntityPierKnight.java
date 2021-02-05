/*==============================================================================
 = Class: EntityPierKnight
 = This class is part of Metallurgy 4: Reforged
 = Complete source code is available at https://github.com/Davoleo/Metallurgy-4-Reforged
 = This code is licensed under GNU GPLv3
 = Authors: Davoleo, ItHurtsLikeHell, PierKnight100
 = Copyright (c) 2018-2021.
 =============================================================================*/

package it.hurts.metallurgy_reforged.entity;

import com.google.common.base.Optional;
import com.google.common.collect.Streams;
import it.hurts.metallurgy_reforged.effect.MetallurgyEffects;
import it.hurts.metallurgy_reforged.entity.ai.AIPierKnightFollow;
import it.hurts.metallurgy_reforged.entity.ai.AIPierOwnerAttack;
import it.hurts.metallurgy_reforged.entity.ai.AIPierOwnerWasHurt;
import it.hurts.metallurgy_reforged.material.ModMetals;
import it.hurts.metallurgy_reforged.model.EnumTools;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

@SuppressWarnings("Guava")
public class EntityPierKnight extends EntityCreature implements IEntityOwnable {

    private static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(EntityPierKnight.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    // ;)))
    private static final DataParameter<Byte> THICKNESS = EntityDataManager.createKey(EntityPierKnight.class, DataSerializers.BYTE);
    protected static final DataParameter<Boolean> IS_PUTIN = EntityDataManager.createKey(EntityPierKnight.class, DataSerializers.BOOLEAN);
    private int timeUntilDeath = 20;

    public EntityPierKnight(World worldIn)
    {
        super(worldIn);
        this.setSize(0.6F, 1.8F);
        //this.setCustomNameTag("PierKnight");
    }

    public EntityPierKnight(World worldIn, EntityLivingBase owner, EntityLivingBase attacker, byte thickness)
    {
        //Call the generic constructor
        this(worldIn);
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.of(owner.getUniqueID()));
        this.setAttackTarget(attacker);
        this.timeUntilDeath = 20 * 30;
        this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(ModMetals.DAMASCUS_STEEL.getTool(EnumTools.SWORD)));
        setThickness(thickness);

        @SuppressWarnings("UnstableApiUsage")
        boolean isPutin = Streams.stream(owner.getArmorInventoryList())
                .anyMatch(item -> item.getDisplayName().toLowerCase().contains("putin"));
        this.dataManager.set(IS_PUTIN, isPutin);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
        this.dataManager.register(THICKNESS, (byte) 1);
        this.dataManager.register(IS_PUTIN, false);
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(6, new AIPierKnightFollow(this));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.tasks.addTask(8, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(9, new EntityAIAttackMelee(this, 1.0D, true));

        this.targetTasks.addTask(1, new AIPierOwnerWasHurt(this));
        this.targetTasks.addTask(2, new AIPierOwnerAttack(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
        super.initEntityAI();
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
    }

    @Nullable
    @Override
    public IEntityLivingData onInitialSpawn(@Nonnull DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
        setThickness(dataManager.get(THICKNESS));
        setHeldItem(EnumHand.MAIN_HAND, new ItemStack(ModMetals.DAMASCUS_STEEL.getTool(EnumTools.SWORD)));
        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn)
    {
        float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        int i = 0;

        if (entityIn instanceof EntityLivingBase)
        {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase) entityIn).getCreatureAttribute());
            i += EnchantmentHelper.getKnockbackModifier(this);
        }

        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

        if (flag)
        {
            if (i > 0)
            {
                ((EntityLivingBase) entityIn).knockBack(this, (float) i * 0.5F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);

            if (j > 0)
            {
                entityIn.setFire(j * 4);
            }

            if (entityIn instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer) entityIn;
                ItemStack itemstack = this.getHeldItemMainhand();
                ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

                if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem().canDisableShield(itemstack, itemstack1, entityplayer, this) && itemstack1.getItem().isShield(itemstack1, entityplayer))
                {
                    float f1 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

                    if (this.rand.nextFloat() < f1)
                    {
                        entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
                        this.world.setEntityState(entityplayer, (byte) 30);
                    }
                }
            }

            this.applyEnchantments(this, entityIn);
        }

        return flag;
    }

    @Nonnull
    @Override
    public EnumHandSide getPrimaryHand()
    {
        return EnumHandSide.RIGHT;
    }

    @Override
    protected boolean canBeRidden(@Nonnull Entity entityIn)
    {
        return false;
    }

    @Override
    public boolean canBeLeashedTo(@Nonnull EntityPlayer player)
    {
        return false;
    }

    @Override
    public void onDeath(@Nonnull DamageSource cause)
    {
        super.onDeath(cause);
        EntityLivingBase owner = (EntityLivingBase) getOwner();
        if (owner == null)
            return;

        if (owner.getEntityData().getBoolean("has_pier"))
        {
            owner.getEntityData().setBoolean("has_pier", false);
            //Set the armor on cooldown
            owner.getArmorInventoryList().forEach(stack -> ((EntityPlayerMP) owner).getCooldownTracker().setCooldown(stack.getItem(), 200));
        }
    }

    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        this.updateArmSwingProgress();

        //If the time is up (it means the council has decided pier should die)
        if (!world.isRemote)
        {
            if (this.getAttackTarget() == null)
                timeUntilDeath--;


            boolean canPierExist = MetallurgyEffects.damascusSteelArmorEffect.canBeApplied((EntityLivingBase) getOwner());
            if (timeUntilDeath <= 0 || !canPierExist)
            {
                this.attackEntityFrom(DamageSource.GENERIC, Integer.MAX_VALUE);
            }
        }
    }

    @Override
    public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn)
    {
        timeUntilDeath = 600;
        super.setAttackTarget(entitylivingbaseIn);
    }

    @Override
    protected boolean canDespawn()
    {
        return false;
    }

    public void setThickness(byte thickness)
    {
        this.dataManager.set(THICKNESS, thickness);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10 * dataManager.get(THICKNESS));
    }

    @Nullable
    @Override
    public UUID getOwnerId()
    {
        return dataManager.get(OWNER_UNIQUE_ID).orNull();
    }

    @Nullable
    @Override
    public Entity getOwner()
    {
        try
        {
            UUID uuid = this.getOwnerId();
            return uuid == null || world.isRemote ? null : ((WorldServer) this.world).getEntityFromUuid(uuid);
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

        //if something goes wrong with the 8 here, that's because I have no idea why I put it here
        if (compound.hasKey("OwnerUUID", 8))
        {
            Optional<UUID> thing = Optional.of(UUID.fromString(compound.getString("OwnerUUID")));
            dataManager.set(OWNER_UNIQUE_ID, thing);
        }

        setThickness(compound.getByte("PierThickness"));

        if (compound.hasKey("PierLifespan"))
            timeUntilDeath = compound.getInteger("PierLifespan");

        dataManager.set(IS_PUTIN, compound.getBoolean("WidePutin"));

    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

        if (this.getOwnerId() == null)
            compound.setString("OwnerUUID", "");
        else
            compound.setString("OwnerUUID", this.getOwnerId().toString());

        compound.setByte("PierThickness", this.dataManager.get(THICKNESS));

        compound.setInteger("PierLifespan", timeUntilDeath);

        compound.setBoolean("WidePutin", dataManager.get(IS_PUTIN));
    }
}
