package io.github.ocelot.network;

import io.github.ocelot.painting.Painting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    @OnlyIn(Dist.CLIENT)
    public Painting getPainting()
    {
        return painting;
    }
}
