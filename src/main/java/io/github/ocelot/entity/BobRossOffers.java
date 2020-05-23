package io.github.ocelot.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;

/**
 * @author Ocelot
 */
public class BobRossOffers extends MerchantOffers
{
    public BobRossOffers()
    {
        super();
    }

    public BobRossOffers(CompoundNBT nbt)
    {
        ListNBT listnbt = nbt.getList("Recipes", 10);

        for (int i = 0; i < listnbt.size(); ++i)
        {
            CompoundNBT offerNbt = listnbt.getCompound(i);
            if (!offerNbt.isEmpty())
            {
                this.add(new MerchantOffer(listnbt.getCompound(i)));
            }
            else
            {
                this.add(new TeleportationPaintingOffer());
            }
        }
    }

    @Override
    public void write(PacketBuffer buffer)
    {
        buffer.writeByte((byte) (this.size() & 255));

        for (int i = 0; i < this.size(); ++i)
        {
            MerchantOffer merchantoffer = this.get(i);
            buffer.writeBoolean(merchantoffer instanceof TeleportationPaintingOffer);
            if (!(merchantoffer instanceof TeleportationPaintingOffer))
            {
                buffer.writeItemStack(merchantoffer.getBuyingStackFirst());
                buffer.writeItemStack(merchantoffer.getSellingStack());
                ItemStack itemstack = merchantoffer.getBuyingStackSecond();
                buffer.writeBoolean(!itemstack.isEmpty());
                if (!itemstack.isEmpty())
                {
                    buffer.writeItemStack(itemstack);
                }

                buffer.writeBoolean(merchantoffer.hasNoUsesLeft());
                buffer.writeInt(merchantoffer.getUses());
                buffer.writeInt(merchantoffer.func_222214_i());
                buffer.writeInt(merchantoffer.getGivenExp());
                buffer.writeInt(merchantoffer.getSpecialPrice());
                buffer.writeFloat(merchantoffer.getPriceMultiplier());
                buffer.writeInt(merchantoffer.getDemand());
            }
        }

    }

    public static BobRossOffers read(PacketBuffer buffer)
    {
        BobRossOffers merchantoffers = new BobRossOffers();
        int i = buffer.readByte() & 255;

        for (int j = 0; j < i; ++j)
        {
            if (buffer.readBoolean())
            {
                merchantoffers.add(new TeleportationPaintingOffer());
            }
            else
            {
                ItemStack itemstack = buffer.readItemStack();
                ItemStack itemstack1 = buffer.readItemStack();
                ItemStack itemstack2 = ItemStack.EMPTY;
                if (buffer.readBoolean())
                {
                    itemstack2 = buffer.readItemStack();
                }

                boolean flag = buffer.readBoolean();
                int k = buffer.readInt();
                int l = buffer.readInt();
                int i1 = buffer.readInt();
                int j1 = buffer.readInt();
                float f = buffer.readFloat();
                int k1 = buffer.readInt();
                MerchantOffer merchantoffer = new MerchantOffer(itemstack, itemstack2, itemstack1, k, l, i1, f, k1);
                if (flag)
                {
                    merchantoffer.getMaxUses();
                }

                merchantoffer.setSpecialPrice(j1);
                merchantoffers.add(merchantoffer);
            }
        }

        return merchantoffers;
    }
}
