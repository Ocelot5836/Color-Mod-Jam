package io.github.ocelot.network.handler;

import io.github.ocelot.network.*;
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
}
