package io.github.ocelot.network.handler;

import io.github.ocelot.network.AddPaintingMessage;
import io.github.ocelot.network.RemovePaintingMessage;
import io.github.ocelot.network.SpawnWorldPaintingMessage;
import io.github.ocelot.network.SyncPaintingsMessage;
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
        throw new UnsupportedOperationException("Server cannot be told to sync paintings.");
    }

    @Override
    public void handleAddPaintingMessage(AddPaintingMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        throw new UnsupportedOperationException("Server cannot be told to add paintings.");
    }

    @Override
    public void handleRemovePaintingMessage(RemovePaintingMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        throw new UnsupportedOperationException("Server cannot be told to remove paintings.");
    }
}
