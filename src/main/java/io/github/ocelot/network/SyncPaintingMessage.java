package io.github.ocelot.network;

import io.github.ocelot.painting.Painting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;

/**
 * @author Ocelot
 */
public class SyncPaintingMessage
{
    private final Painting painting;

    public SyncPaintingMessage(Painting painting)
    {
        this.painting = painting;
    }

    public static void encode(SyncPaintingMessage msg, PacketBuffer buf)
    {
        buf.writeCompoundTag(msg.painting.serializeNBT());
    }

    public static SyncPaintingMessage decode(PacketBuffer buf)
    {
        CompoundNBT nbt = buf.readCompoundTag();
        if (nbt == null)
            return new SyncPaintingMessage(null);
        return new SyncPaintingMessage(new Painting(nbt));
    }

    @Nullable
    public Painting getPainting()
    {
        return painting;
    }
}
