package io.github.ocelot.network;

import io.github.ocelot.painting.Painting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class AddPaintingMessage
{
    private final Painting painting;

    public AddPaintingMessage(Painting painting)
    {
        this.painting = painting;
    }

    public static void encode(AddPaintingMessage msg, PacketBuffer buf)
    {
        buf.writeCompoundTag(msg.painting.serializeNBT());
    }

    public static AddPaintingMessage decode(PacketBuffer buf)
    {
        CompoundNBT nbt = buf.readCompoundTag();
        if (nbt == null)
            return new AddPaintingMessage(null);
        return new AddPaintingMessage(new Painting(nbt));
    }

    public Painting getPainting()
    {
        return painting;
    }
}
