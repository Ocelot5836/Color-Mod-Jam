package io.github.ocelot.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Ocelot
 */
public class SelectBobRossTradeMessage
{
    private final int selectedMerchantRecipe;

    public SelectBobRossTradeMessage(int selectedMerchantRecipe)
    {
        this.selectedMerchantRecipe = selectedMerchantRecipe;
    }

    public static void encode(SelectBobRossTradeMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.selectedMerchantRecipe);
    }

    public static SelectBobRossTradeMessage decode(PacketBuffer buf)
    {
        return new SelectBobRossTradeMessage(buf.readInt());
    }

    @OnlyIn(Dist.CLIENT)
    public int getSelectedMerchantRecipe()
    {
        return selectedMerchantRecipe;
    }
}
