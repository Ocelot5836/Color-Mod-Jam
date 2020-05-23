package io.github.ocelot.network.handler;

import io.github.ocelot.container.BobRossTradeContainer;
import io.github.ocelot.network.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @author Ocelot
 */
public class ServerMessageHandler implements MessageHandler
{
    public static final MessageHandler INSTANCE = new ServerMessageHandler();

    private ServerMessageHandler()
    {
    }

    @Override
    public void handleSpawnWorldPaintingMessage(SpawnWorldPaintingMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        throw new UnsupportedOperationException("Server cannot be told to spawn world painting.");
    }

    @Override
    public void handleSyncPaintingsMessage(SyncPaintingsMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        throw new UnsupportedOperationException("Server cannot be told to sync world paintings.");
    }

    @Override
    public void handleSyncPaintingMessage(SyncPaintingMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        throw new UnsupportedOperationException("Server cannot be told to sync world paintings.");
    }

    @Override
    public void handleAddPaintingMessage(AddPaintingMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        throw new UnsupportedOperationException("Server cannot be told to add world paintings.");
    }

    @Override
    public void handleRemovePaintingMessage(RemovePaintingMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        throw new UnsupportedOperationException("Server cannot be told to remove world paintings.");
    }

    @Override
    public void handleSyncPaintingRealmsMessage(SyncPaintingRealmsMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        throw new UnsupportedOperationException("Server cannot be told to remove world painting realms.");
    }

    @Override
    public void handleAddPaintingRealmMessage(AddPaintingRealmMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        throw new UnsupportedOperationException("Server cannot be told to add world painting realms.");
    }

    @Override
    public void handleSelectBobRossTradeMessage(SelectBobRossTradeMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity player = ctx.get().getSender();

        ctx.get().enqueueWork(() ->
        {
            if (player == null)
                return;
            Container container = player.openContainer;
            if (container instanceof BobRossTradeContainer)
            {
                BobRossTradeContainer bobRossTradeContainer = (BobRossTradeContainer) container;
                bobRossTradeContainer.setCurrentRecipeIndex(msg.getSelectedMerchantRecipe());
                bobRossTradeContainer.func_217046_g(msg.getSelectedMerchantRecipe());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
