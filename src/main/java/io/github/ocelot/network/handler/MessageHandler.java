package io.github.ocelot.network.handler;

import io.github.ocelot.network.*;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @author Ocelot
 */
public interface MessageHandler
{
    void handleSpawnWorldPaintingMessage(SpawnWorldPaintingMessage msg, Supplier<NetworkEvent.Context> ctx);

    void handleSyncPaintingsMessage(SyncPaintingsMessage msg, Supplier<NetworkEvent.Context> ctx);

    void handleSyncPaintingMessage(SyncPaintingMessage msg, Supplier<NetworkEvent.Context> ctx);

    void handleAddPaintingMessage(AddPaintingMessage msg, Supplier<NetworkEvent.Context> ctx);

    void handleRemovePaintingMessage(RemovePaintingMessage msg, Supplier<NetworkEvent.Context> ctx);

    void handleSyncPaintingRealmsMessage(SyncPaintingRealmsMessage msg, Supplier<NetworkEvent.Context> ctx);

    void handleAddPaintingRealmMessage(AddPaintingRealmMessage msg, Supplier<NetworkEvent.Context> ctx);

    void handleSelectBobRossTradeMessage(SelectBobRossTradeMessage msg, Supplier<NetworkEvent.Context> ctx);
}