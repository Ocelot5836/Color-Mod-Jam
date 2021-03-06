package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.network.*;
import io.github.ocelot.network.handler.ClientMessageHandler;
import io.github.ocelot.network.handler.MessageHandler;
import io.github.ocelot.network.handler.ServerMessageHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PainterMessages
{
    public static final String VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(WorldPainter.MOD_ID, "instance"), () -> VERSION, VERSION::equals, VERSION::equals);

    private static int index;

    public static void init()
    {
        registerMessage(SpawnWorldPaintingMessage.class, SpawnWorldPaintingMessage::encode, SpawnWorldPaintingMessage::decode, (msg, ctx) -> getHandler(ctx).handleSpawnWorldPaintingMessage(msg, ctx));
        registerMessage(SyncPaintingsMessage.class, SyncPaintingsMessage::encode, SyncPaintingsMessage::decode, (msg, ctx) -> getHandler(ctx).handleSyncPaintingsMessage(msg, ctx));
        registerMessage(SyncPaintingMessage.class, SyncPaintingMessage::encode, SyncPaintingMessage::decode, (msg, ctx) -> getHandler(ctx).handleSyncPaintingMessage(msg, ctx));
        registerMessage(AddPaintingMessage.class, AddPaintingMessage::encode, AddPaintingMessage::decode, (msg, ctx) -> getHandler(ctx).handleAddPaintingMessage(msg, ctx));
        registerMessage(RemovePaintingMessage.class, RemovePaintingMessage::encode, RemovePaintingMessage::decode, (msg, ctx) -> getHandler(ctx).handleRemovePaintingMessage(msg, ctx));
        registerMessage(SyncPaintingRealmsMessage.class, SyncPaintingRealmsMessage::encode, SyncPaintingRealmsMessage::decode, (msg, ctx) -> getHandler(ctx).handleSyncPaintingRealmsMessage(msg, ctx));
        registerMessage(AddPaintingRealmMessage.class, AddPaintingRealmMessage::encode, AddPaintingRealmMessage::decode, (msg, ctx) -> getHandler(ctx).handleAddPaintingRealmMessage(msg, ctx));
        registerMessage(SelectBobRossTradeMessage.class, SelectBobRossTradeMessage::encode, SelectBobRossTradeMessage::decode, (msg, ctx) -> getHandler(ctx).handleSelectBobRossTradeMessage(msg, ctx));
    }

    private static <MSG> void registerMessage(Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer)
    {
        INSTANCE.registerMessage(index++, messageType, encoder, decoder, messageConsumer);
    }

    private static MessageHandler getHandler(Supplier<NetworkEvent.Context> ctx)
    {
        return ctx.get().getDirection().getReceptionSide().isClient() ? ClientMessageHandler.INSTANCE : ServerMessageHandler.INSTANCE;
    }
}
