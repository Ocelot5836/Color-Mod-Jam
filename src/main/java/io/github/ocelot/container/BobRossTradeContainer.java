package io.github.ocelot.container;

import io.github.ocelot.entity.BobRossOffers;
import io.github.ocelot.init.PainterContainers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.NPCMerchant;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.MerchantResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Ocelot
 */
public class BobRossTradeContainer extends Container
{
    private final IMerchant merchant;
    private final BobRossMerchantInventory merchantInventory;
    @OnlyIn(Dist.CLIENT)
    private boolean showLocked;

    public BobRossTradeContainer(int id, PlayerInventory playerInventoryIn)
    {
        this(id, playerInventoryIn, new NPCMerchant(playerInventoryIn.player));
    }

    public BobRossTradeContainer(int id, PlayerInventory playerInventoryIn, IMerchant merchantIn)
    {
        super(PainterContainers.BOB_ROSS.get(), id);
        this.merchant = merchantIn;
        this.merchantInventory = new BobRossMerchantInventory(merchantIn);
        this.addSlot(new Slot(this.merchantInventory, 0, 136, 37));
        this.addSlot(new Slot(this.merchantInventory, 1, 162, 37));
        this.addSlot(new MerchantResultSlot(playerInventoryIn.player, merchantIn, this.merchantInventory, 2, 220, 37));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(playerInventoryIn, j + i * 9 + 9, 108 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlot(new Slot(playerInventoryIn, k, 108 + k * 18, 142));
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.merchantInventory.resetRecipeAndSlots();
        super.onCraftMatrixChanged(inventoryIn);
    }

    public void setCurrentRecipeIndex(int currentRecipeIndex)
    {
        this.merchantInventory.setCurrentRecipeIndex(currentRecipeIndex);
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return this.merchant.getCustomer() == playerIn;
    }

    @OnlyIn(Dist.CLIENT)
    public void setShowLocked(boolean showLocked)
    {
        this.showLocked = showLocked;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean showLocked()
    {
        return this.showLocked;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is
     * null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return false;
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 2)
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
                this.playMerchantYesSound();
            }
            else if (index != 0 && index != 1)
            {
                if (index < 30)
                {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    private void playMerchantYesSound()
    {
        if (!this.merchant.getWorld().isRemote())
        {
            Entity entity = (Entity) this.merchant;
            this.merchant.getWorld().playSound(entity.getPosX(), entity.getPosY(), entity.getPosZ(), this.merchant.getYesSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
        }

    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(PlayerEntity player)
    {
        super.onContainerClosed(player);
        this.merchant.setCustomer(null);
        if (!this.merchant.getWorld().isRemote())
        {
            if (!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).hasDisconnected())
            {
                ItemStack itemstack = this.merchantInventory.removeStackFromSlot(0);
                if (!itemstack.isEmpty())
                {
                    player.dropItem(itemstack, false);
                }

                itemstack = this.merchantInventory.removeStackFromSlot(1);
                if (!itemstack.isEmpty())
                {
                    player.dropItem(itemstack, false);
                }
            }
            else
            {
                player.inventory.placeItemBackInInventory(player.world, this.merchantInventory.removeStackFromSlot(0));
                player.inventory.placeItemBackInInventory(player.world, this.merchantInventory.removeStackFromSlot(1));
            }
        }
    }

    public void func_217046_g(int p_217046_1_)
    {
        if (this.getOffers().size() > p_217046_1_)
        {
            ItemStack itemstack = this.merchantInventory.getStackInSlot(0);
            if (!itemstack.isEmpty())
            {
                if (!this.mergeItemStack(itemstack, 3, 39, true))
                {
                    return;
                }

                this.merchantInventory.setInventorySlotContents(0, itemstack);
            }

            ItemStack itemstack1 = this.merchantInventory.getStackInSlot(1);
            if (!itemstack1.isEmpty())
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return;
                }

                this.merchantInventory.setInventorySlotContents(1, itemstack1);
            }

            if (this.merchantInventory.getStackInSlot(0).isEmpty() && this.merchantInventory.getStackInSlot(1).isEmpty())
            {
                ItemStack itemstack2 = this.getOffers().get(p_217046_1_).func_222205_b();
                this.func_217053_c(0, itemstack2);
                ItemStack itemstack3 = this.getOffers().get(p_217046_1_).getBuyingStackSecond();
                this.func_217053_c(1, itemstack3);
            }

        }
    }

    private void func_217053_c(int p_217053_1_, ItemStack p_217053_2_)
    {
        if (!p_217053_2_.isEmpty())
        {
            for (int i = 3; i < 39; ++i)
            {
                ItemStack itemstack = this.inventorySlots.get(i).getStack();
                if (!itemstack.isEmpty() && this.areItemStacksEqual(p_217053_2_, itemstack))
                {
                    ItemStack itemstack1 = this.merchantInventory.getStackInSlot(p_217053_1_);
                    int j = itemstack1.isEmpty() ? 0 : itemstack1.getCount();
                    int k = Math.min(p_217053_2_.getMaxStackSize() - j, itemstack.getCount());
                    ItemStack itemstack2 = itemstack.copy();
                    int l = j + k;
                    itemstack.shrink(k);
                    itemstack2.setCount(l);
                    this.merchantInventory.setInventorySlotContents(p_217053_1_, itemstack2);
                    if (l >= p_217053_2_.getMaxStackSize())
                    {
                        break;
                    }
                }
            }
        }
    }

    // TODO allow clicking of paintings into slot
    private boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2)
    {
        return stack1.getItem() == stack2.getItem() && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    @OnlyIn(Dist.CLIENT)
    public void setClientSideOffers(BobRossOffers offers)
    {
        this.merchant.setClientSideOffers(offers);
    }

    public MerchantOffers getOffers()
    {
        return this.merchant.getOffers();
    }
}
