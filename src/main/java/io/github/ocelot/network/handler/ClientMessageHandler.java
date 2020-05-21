package io.github.ocelot.network.handler;

import io.github.ocelot.entity.WorldPaintingEntity;
import io.github.ocelot.network.*;
import io.github.ocelot.painting.ClientPaintingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @author Ocelot
 */
public class ClientMessageHandler implements MessageHandler
{
    public static final MessageHandler INSTANCE = new ClientMessageHandler();

    private ClientMessageHandler()
    {
    }

    @Override
    public void handleSpawnWorldPaintingMessage(SpawnWorldPaintingMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        ClientWorld world = Minecraft.getInstance().world;

        ctx.get().enqueueWork(() ->
        {
            if (world == null)
                return;
            WorldPaintingEntity paintingentity = new WorldPaintingEntity(world, msg.getPosition(), msg.getFacing(), msg.getPaintingId());
            paintingentity.setEntityId(msg.getEntityID());
            paintingentity.setUniqueId(msg.getUniqueId());
            world.addEntity(msg.getEntityID(), paintingentity);
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void handleSyncPaintingsMessage(SyncPaintingsMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> ClientPaintingManager.INSTANCE.receivePaintings(msg.getIndex(), msg.getPaintings()));
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void handleSyncPaintingMessage(SyncPaintingMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> ClientPaintingManager.INSTANCE.receivePainting(msg.getPainting()));
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void handleAddPaintingMessage(AddPaintingMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> ClientPaintingManager.INSTANCE.receiveAddPainting(msg.getPainting()));
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void handleRemovePaintingMessage(RemovePaintingMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> ClientPaintingManager.INSTANCE.receiveRemovePainting(msg.getId()));
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void handleSyncPaintingRealmsMessage(SyncPaintingRealmsMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> ClientPaintingManager.INSTANCE.receivePaintingRealms(msg.getIndex(), msg.getPaintingRealmPositions()));
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void handleAddPaintingRealmMessage(AddPaintingRealmMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> ClientPaintingManager.INSTANCE.receiveAddPaintingRealm(msg.getPaintingId(), msg.getRealmId()));
        ctx.get().setPacketHandled(true);
    }
}
