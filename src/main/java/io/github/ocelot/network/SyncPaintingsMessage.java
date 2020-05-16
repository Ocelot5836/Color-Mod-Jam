package io.github.ocelot.network;

import io.github.ocelot.painting.Painting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ocelot
 */
public class SyncPaintingsMessage
{
    private final Collection<Painting> paintings;

    public SyncPaintingsMessage(Collection<Painting> paintings)
    {
        this.paintings = paintings;
    }

    public static void encode(SyncPaintingsMessage msg, PacketBuffer buf)
    {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT list = new ListNBT();
        for (Painting painting : msg.paintings)
            list.add(painting.serializeNBT());
        nbt.put("paintings", list);
        buf.writeCompoundTag(nbt);
    }

    public static SyncPaintingsMessage decode(PacketBuffer buf)
    {
        CompoundNBT nbt = buf.readCompoundTag();
        if (nbt == null)
            return new SyncPaintingsMessage(Collections.emptySet());
        ListNBT list = nbt.getList("paintings", Constants.NBT.TAG_COMPOUND);
        Set<Painting> paintings = new HashSet<>();
        for (int i = 0; i < list.size(); i++)
            paintings.add(new Painting(list.getCompound(i)));
        return new SyncPaintingsMessage(paintings);
    }

    @OnlyIn(Dist.CLIENT)
    public Collection<Painting> getPaintings()
    {
        return paintings;
    }
}
