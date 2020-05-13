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
public interface MessageHandler
{
    void handleSpawnWorldPaintingMessage(SpawnWorldPaintingMessage msg, Supplier<NetworkEvent.Context> ctx);

    void handleSyncPaintingsMessage(SyncPaintingsMessage msg, Supplier<NetworkEvent.Context> ctx);

    void handleAddPaintingMessage(AddPaintingMessage msg, Supplier<NetworkEvent.Context> ctx);

    void handleRemovePaintingMessage(RemovePaintingMessage msg, Supplier<NetworkEvent.Context> ctx);
}