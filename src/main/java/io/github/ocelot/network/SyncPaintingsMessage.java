package io.github.ocelot.network;

import com.google.common.collect.Iterables;
import io.github.ocelot.init.PainterMessages;
import io.github.ocelot.painting.Painting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @author Ocelot
 */
public class SyncPaintingsMessage
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_LIST_SIZE = 100;

    private final int index;
    private final Collection<Painting> paintings;

    private SyncPaintingsMessage(int index, Collection<Painting> paintings)
    {
        this.index = index;
        this.paintings = paintings;
    }

    public static void sendTo(PacketDistributor.PacketTarget target, Collection<Painting> paintings)
    {
        Iterable<List<Painting>> paintingsList = Iterables.partition(paintings, MAX_LIST_SIZE);
        int size = Iterables.size(paintingsList);
        LOGGER.debug("Partitioning sync paintings message into " + size + " segments.");
        for (int i = 0; i < size; i++)
        {
            List<Painting> list = Iterables.get(paintingsList, i);
            PainterMessages.INSTANCE.send(target, new SyncPaintingsMessage(i, list));
        }
    }

    public static void encode(SyncPaintingsMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.index);
        CompoundNBT nbt = new CompoundNBT();
        ListNBT list = new ListNBT();
        for (Painting painting : msg.paintings)
            list.add(painting.serializeNBT());
        nbt.put("paintings", list);
        buf.writeCompoundTag(nbt);
    }

    public static SyncPaintingsMessage decode(PacketBuffer buf)
    {
        int index = buf.readInt();
        CompoundNBT nbt = buf.readCompoundTag();
        if (nbt == null)
            return new SyncPaintingsMessage(index, Collections.emptySet());
        ListNBT list = nbt.getList("paintings", Constants.NBT.TAG_COMPOUND);
        Set<Painting> paintings = new HashSet<>();
        for (int i = 0; i < list.size(); i++)
            paintings.add(new Painting(list.getCompound(i)));
        return new SyncPaintingsMessage(index, paintings);
    }

    @OnlyIn(Dist.CLIENT)
    public int getIndex()
    {
        return index;
    }

    @OnlyIn(Dist.CLIENT)
    public Collection<Painting> getPaintings()
    {
        return paintings;
    }
}
