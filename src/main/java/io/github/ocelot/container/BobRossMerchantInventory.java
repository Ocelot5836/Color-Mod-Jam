package io.github.ocelot.container;

import io.github.ocelot.entity.TeleportationPaintingOffer;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.MerchantInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

public class BobRossMerchantInventory extends MerchantInventory
{
    private final IMerchant merchant;
    private final NonNullList<ItemStack> slots = NonNullList.withSize(3, ItemStack.EMPTY);
    private MerchantOffer offer;
    private int currentRecipeIndex;

    public BobRossMerchantInventory(IMerchant merchantIn)
    {
        super(merchantIn);
        this.merchant = merchantIn;
    }

    @Override
    public int getSizeInventory()
    {
        return this.slots.size();
    }

    @Override
    public boolean isEmpty()
    {
        for (ItemStack itemstack : this.slots)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return this.slots.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = this.slots.get(index);
        if (index == 2 && !itemstack.isEmpty())
        {
            return ItemStackHelper.getAndSplit(this.slots, index, itemstack.getCount());
        }
        else
        {
            ItemStack itemstack1 = ItemStackHelper.getAndSplit(this.slots, index, count);
            if (!itemstack1.isEmpty() && this.inventoryResetNeededOnSlotChange(index))
            {
                this.resetRecipeAndSlots();
            }

            return itemstack1;
        }
    }

    private boolean inventoryResetNeededOnSlotChange(int slotIn)
    {
        return slotIn == 0 || slotIn == 1;
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(this.slots, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.slots.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }

        if (this.inventoryResetNeededOnSlotChange(index))
        {
            this.resetRecipeAndSlots();
        }

    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player)
    {
        return this.merchant.getCustomer() == player;
    }

    @Override
    public void markDirty()
    {
        this.resetRecipeAndSlots();
    }

    @Override
    public void resetRecipeAndSlots()
    {
        this.offer = null;
        ItemStack itemstack;
        ItemStack itemstack1;
        if (this.slots.get(0).isEmpty())
        {
            itemstack = this.slots.get(1);
            itemstack1 = ItemStack.EMPTY;
        }
        else
        {
            itemstack = this.slots.get(0);
            itemstack1 = this.slots.get(1);
        }

        if (itemstack.isEmpty())
        {
            this.setInventorySlotContents(2, ItemStack.EMPTY);
        }
        else
        {
            MerchantOffers merchantoffers = this.merchant.getOffers();
            if (!merchantoffers.isEmpty())
            {
                MerchantOffer merchantoffer = merchantoffers.func_222197_a(itemstack, itemstack1, this.currentRecipeIndex);
                if (merchantoffer == null || merchantoffer.hasNoUsesLeft())
                {
                    this.offer = merchantoffer;
                    merchantoffer = merchantoffers.func_222197_a(itemstack1, itemstack, this.currentRecipeIndex);
                }

                if (merchantoffer != null && !merchantoffer.hasNoUsesLeft())
                {
                    this.offer = merchantoffer;
                    this.setInventorySlotContents(2, merchantoffer instanceof TeleportationPaintingOffer ? ((TeleportationPaintingOffer) merchantoffer).getCopyOfSellingStack(itemstack, itemstack1) : merchantoffer.getCopyOfSellingStack());
                }
                else
                {
                    this.setInventorySlotContents(2, ItemStack.EMPTY);
                }
            }

            this.merchant.verifySellingItem(this.getStackInSlot(2));
        }
    }

    @Nullable
    @Override
    public MerchantOffer func_214025_g()
    {
        return this.offer;
    }

    @Override
    public void setCurrentRecipeIndex(int currentRecipeIndexIn)
    {
        this.currentRecipeIndex = currentRecipeIndexIn;
        this.resetRecipeAndSlots();
    }

    @Override
    public void clear()
    {
        this.slots.clear();
    }
}