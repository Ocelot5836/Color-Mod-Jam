package io.github.ocelot.entity;

import io.github.ocelot.client.screen.BobRossTradeScreenFactory;
import io.github.ocelot.init.PainterBlocks;
import io.github.ocelot.init.PainterEntities;
import io.github.ocelot.init.PainterItems;
import io.github.ocelot.item.PaintBucketItem;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Ocelot
 */
public class BobRossEntity extends AbstractVillagerEntity implements IShearable
{
    private static final DataParameter<Boolean> SHEARED = EntityDataManager.createKey(BobRossEntity.class, DataSerializers.BOOLEAN);

    private static final int MIN_SPEAK_DELAY = 400;
    private static final int MAX_SPEAK_DELAY = 600;
    private static final VillagerTrades.ITrade[] TELEPORTATION_PAINTING_TRADES = {(trader, rand) -> new TeleportationPaintingOffer()};

    private int speakingTicks;

    public BobRossEntity(EntityType<? extends AbstractVillagerEntity> type, World world)
    {
        super(type, world);
    }

    private void speak(PlayerEntity player)
    {
        // TODO say lines to the specific player
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(SHEARED, false);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (!this.world.isRemote())
        {
            if (this.speakingTicks > 0)
                this.speakingTicks--;
            if (this.hasCustomer() && this.speakingTicks == 0)
            {
                this.speakingTicks = MIN_SPEAK_DELAY + this.rand.nextInt(MAX_SPEAK_DELAY - MIN_SPEAK_DELAY);
                this.speak(Objects.requireNonNull(this.getCustomer()));
            }
        }
    }

    @Override
    protected void onVillagerTrade(MerchantOffer offer)
    {
    }

    @Override
    public boolean func_213705_dZ()
    {
        return false;
    }

    @Override
    public MerchantOffers getOffers()
    {
        if (this.offers == null)
        {
            this.offers = new BobRossOffers();
            this.populateTradeData();
        }

        return this.offers;
    }

    @Override
    protected void populateTradeData()
    {
        MerchantOffers offers = this.getOffers();
        this.addTrades(this.getOffers(), TELEPORTATION_PAINTING_TRADES, 1);
        offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 4), new ItemStack(PainterItems.SMALL_PAINT_BRUSH.get()), Integer.MAX_VALUE, 0, 0.05F));
        offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 12), new ItemStack(PainterItems.MEDIUM_PAINT_BRUSH.get()), Integer.MAX_VALUE, 0, 0.05F));
        offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 16), new ItemStack(PainterItems.LARGE_PAINT_BRUSH.get()), Integer.MAX_VALUE, 0, 0.05F));
        for (DyeColor color : DyeColor.values())
        {
            ItemStack result = new ItemStack(PainterBlocks.PAINT_BUCKET.get());
            ((PaintBucketItem) PainterBlocks.PAINT_BUCKET.get().asItem()).setColor(result, color.getColorValue());
            offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 6), result, Integer.MAX_VALUE, 0, 0.05F));
        }
    }

    @Nullable
    @Override
    public AgeableEntity createChild(AgeableEntity ageable)
    {
        return null;
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);
        if (itemstack.interactWithEntity(player, this, hand))
        {
            return true;
        }
        else if (this.isAlive() && !this.hasCustomer() && !this.isChild() && !this.isSheared())
        {
            if (hand == Hand.MAIN_HAND)
            {
                player.addStat(Stats.TALKED_TO_VILLAGER);
            }

            if (this.getOffers().isEmpty())
            {
                return super.processInteract(player, hand);
            }
            else
            {
                if (!this.world.isRemote())
                {
                    this.setCustomer(player);
                    BobRossTradeScreenFactory.openContainer((ServerPlayerEntity) player, this);
                }

                return true;
            }
        }
        else
        {
            return super.processInteract(player, hand);
        }
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
        return new ItemStack(PainterEntities.getSpawnEgg(PainterEntities.BOB_ROSS));
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, IWorldReader world, BlockPos pos)
    {
        return !this.isSheared();
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IWorld world, BlockPos pos, int fortune)
    {
        this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
        if (!this.world.isRemote())
        {
            this.getDataManager().set(SHEARED, true);
            return Collections.singletonList(new ItemStack(PainterItems.AFRO.get()));
        }
        return IShearable.super.onSheared(item, world, pos, fortune);
    }

    public boolean isSheared()
    {
        return this.getDataManager().get(SHEARED);
    }

    @Override
    public void readAdditional(CompoundNBT nbt)
    {
        super.readAdditional(nbt);
        if (nbt.contains("Offers", 10))
        {
            this.offers = new BobRossOffers(nbt.getCompound("Offers"));
        }
    }
}
