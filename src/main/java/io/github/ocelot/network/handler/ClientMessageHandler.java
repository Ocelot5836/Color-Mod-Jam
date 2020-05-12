package io.github.ocelot.network.handler;

import io.github.ocelot.entity.WorldPaintingEntity;
import io.github.ocelot.network.AddPaintingMessage;
import io.github.ocelot.network.RemovePaintingMessage;
import io.github.ocelot.network.SpawnWorldPaintingMessage;
import io.github.ocelot.network.SyncPaintingsMessage;
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
        ctx.get().enqueueWork(() -> ClientPaintingManager.INSTANCE.receivePaintings(msg.getPaintings()));
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
}
