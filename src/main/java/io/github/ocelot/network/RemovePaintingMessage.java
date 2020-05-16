package io.github.ocelot.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

/**
 * @author Ocelot
 */
public class RemovePaintingMessage
{
    private final UUID id;

    public RemovePaintingMessage(UUID id)
    {
        this.id = id;
    }

    public static void encode(RemovePaintingMessage msg, PacketBuffer buf)
    {
        buf.writeUniqueId(msg.id);
    }

    public static RemovePaintingMessage decode(PacketBuffer buf)
    {
        return new RemovePaintingMessage(buf.readUniqueId());
    }

    @OnlyIn(Dist.CLIENT)
    public UUID getId()
    {
        return id;
    }
}
