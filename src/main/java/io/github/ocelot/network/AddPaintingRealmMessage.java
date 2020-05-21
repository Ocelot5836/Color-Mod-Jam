package io.github.ocelot.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Ocelot
 */
public class AddPaintingRealmMessage
{
    private final Integer paintingId;
    private final int realmId;

    public AddPaintingRealmMessage(Integer paintingId, int realmId)
    {
        this.paintingId = paintingId;
        this.realmId = realmId;
    }

    public static void encode(AddPaintingRealmMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.paintingId);
        buf.writeInt(msg.realmId);
    }

    public static AddPaintingRealmMessage decode(PacketBuffer buf)
    {
        return new AddPaintingRealmMessage(buf.readInt(), buf.readInt());
    }

    @OnlyIn(Dist.CLIENT)
    public Integer getPaintingId()
    {
        return paintingId;
    }

    @OnlyIn(Dist.CLIENT)
    public int getRealmId()
    {
        return realmId;
    }
}
