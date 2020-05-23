package io.github.ocelot.entity;

import io.github.ocelot.client.screen.BobRossTradeScreenFactory;
import io.github.ocelot.init.PainterEntities;
import io.github.ocelot.init.PainterItems;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author Ocelot
 */
public class BobRossEntity extends AbstractVillagerEntity
{
    private static final int MIN_SPEAK_DELAY = 400;
    private static final int MAX_SPEAK_DELAY = 600;
    private static final VillagerTrades.ITrade[] TELEPORTATION_PAINTING_TRADES = {(trader, rand) -> new TeleportationPaintingOffer()};

    private int speakingTicks;

    public BobRossEntity(EntityType<? extends AbstractVillagerEntity> type, World world)
    {
        super(type, world);
        this.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(PainterItems.AFRO.get()));
    }

    private void speak(PlayerEntity player)
    {
        player.sendMessage(new StringTextComponent("Test"));
        this.world.playSound(null, this.getPosition(), this.getYesSound(), SoundCategory.AMBIENT, 1.0f, 1.0f);
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
        if (offer.getDoesRewardExp())
        {
            this.world.addEntity(new ExperienceOrbEntity(this.world, this.getPosX(), this.getPosY() + 0.5D, this.getPosZ(), 3 + this.rand.nextInt(4)));
        }
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
        this.addTrades(this.getOffers(), TELEPORTATION_PAINTING_TRADES, 1);
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
        boolean flag = itemstack.getItem() == Items.NAME_TAG;
        if (flag)
        {
            itemstack.interactWithEntity(player, this, hand);
            return true;
        }
        else if (this.isAlive() && !this.hasCustomer() && !this.isChild())
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
    public void readAdditional(CompoundNBT nbt)
    {
        super.readAdditional(nbt);
        if (nbt.contains("Offers", 10))
        {
            this.offers = new BobRossOffers(nbt.getCompound("Offers"));
        }
    }
}
