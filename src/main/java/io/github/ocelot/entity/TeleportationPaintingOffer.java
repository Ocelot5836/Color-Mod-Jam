package io.github.ocelot.entity;

import io.github.ocelot.init.PainterItems;
import io.github.ocelot.painting.FixedPaintingType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.nbt.CompoundNBT;

import java.util.UUID;

/**
 * @author Ocelot
 */
public class TeleportationPaintingOffer extends MerchantOffer
{
    public TeleportationPaintingOffer()
    {
        super(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, 0, Integer.MAX_VALUE, 0, 0, 0);
    }

    @Override
    public ItemStack getBuyingStackFirst()
    {
        return new ItemStack(PainterItems.WORLD_PAINTING.get());
    }

    @Override
    public ItemStack func_222205_b()
    {
        return this.getBuyingStackFirst();
    }

    @Override
    public ItemStack getBuyingStackSecond()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getSellingStack()
    {
        return new ItemStack(PainterItems.TELEPORTATION_PAINTING.get());
    }

    @Override
    public void calculateDemand()
    {
    }

    @Override
    public ItemStack getCopyOfSellingStack()
    {
        return this.getSellingStack().copy();
    }

    public ItemStack getCopyOfSellingStack(ItemStack inputA, ItemStack inputB)
    {
        ItemStack output = this.getCopyOfSellingStack();
        if (inputA.getItem() == PainterItems.WORLD_PAINTING.get() && output.getItem() == PainterItems.TELEPORTATION_PAINTING.get())
        {
            if (PainterItems.WORLD_PAINTING.get().hasPainting(inputA))
            {
                UUID paintingId = PainterItems.WORLD_PAINTING.get().getPaintingId(inputA);
                if (FixedPaintingType.isFixed(paintingId))
                {
                    return FixedPaintingType.getType(paintingId).getStack();
                }
                else
                {
                    PainterItems.TELEPORTATION_PAINTING.get().setPainting(output, paintingId);
                }
            }
        }
        return output;
    }

    @Override
    public void increaseUses()
    {
    }

    @Override
    public int getDemand()
    {
        return 0;
    }

    @Override
    public void increaseSpecialPrice(int add)
    {
    }

    @Override
    public void resetSpecialPrice()
    {
    }

    @Override
    public int getSpecialPrice()
    {
        return 0;
    }

    @Override
    public void setSpecialPrice(int price)
    {
    }

    @Override
    public float getPriceMultiplier()
    {
        return 1;
    }

    @Override
    public int getGivenExp()
    {
        return 0;
    }

    @Override
    public boolean getDoesRewardExp()
    {
        return false;
    }

    @Override
    public CompoundNBT write()
    {
        return new CompoundNBT();
    }

    @Override
    public boolean matches(ItemStack left, ItemStack right)
    {
        return left.getItem() == this.func_222205_b().getItem() && left.getCount() >= this.func_222205_b().getCount() && right.getItem() == this.getBuyingStackSecond().getItem() && right.getCount() >= this.getBuyingStackSecond().getCount();
    }

    @Override
    public boolean doTransaction(ItemStack left, ItemStack right)
    {
        if (!this.matches(left, right))
        {
            return false;
        }
        else
        {
            left.shrink(this.func_222205_b().getCount());
            if (!this.getBuyingStackSecond().isEmpty())
            {
                right.shrink(this.getBuyingStackSecond().getCount());
            }

            return true;
        }
    }
}
