package io.github.ocelot.client.screen;

import io.github.ocelot.container.BobRossTradeContainer;
import io.github.ocelot.entity.BobRossEntity;
import io.github.ocelot.entity.BobRossOffers;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.MerchantOffers;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * @author Ocelot
 */
public class BobRossTradeScreenFactory implements ScreenManager.IScreenFactory<BobRossTradeContainer, BobRossTradeScreen>, IContainerFactory<BobRossTradeContainer>
{
    @Override
    public BobRossTradeScreen create(BobRossTradeContainer container, PlayerInventory inventory, ITextComponent title)
    {
        return new BobRossTradeScreen(container, inventory, title);
    }

    @Override
    public BobRossTradeContainer create(int windowId, PlayerInventory playerInventory, PacketBuffer buffer)
    {
        BobRossTradeContainer container = new BobRossTradeContainer(windowId, playerInventory);
        container.setClientSideOffers(BobRossOffers.read(buffer));
        container.setShowLocked(buffer.readBoolean());
        return container;
    }

    public static void openContainer(ServerPlayerEntity player, BobRossEntity bobRoss)
    {
        NetworkHooks.openGui(player, new SimpleNamedContainerProvider((windowId, playerInventory, playerEntity) -> new BobRossTradeContainer(windowId, playerInventory, bobRoss), bobRoss.getDisplayName()), buffer ->
        {
            MerchantOffers merchantOffers = bobRoss.getOffers();
            if (!merchantOffers.isEmpty())
            {
                merchantOffers.write(buffer);
                buffer.writeBoolean(bobRoss.func_223340_ej());
            }
        });
    }
}
