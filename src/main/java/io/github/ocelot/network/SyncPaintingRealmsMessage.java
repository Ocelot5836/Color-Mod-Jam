package io.github.ocelot.network;

import com.google.common.collect.Iterables;
import io.github.ocelot.init.PainterMessages;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Ocelot
 */
public class SyncPaintingRealmsMessage
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_LIST_SIZE = 100;

    private final int index;
    private final Map<Integer, Integer> paintingRealmPositions;

    private SyncPaintingRealmsMessage(int index, Map<Integer, Integer> paintingRealmPositions)
    {
        this.index = index;
        this.paintingRealmPositions = paintingRealmPositions;
    }

    public static void sendTo(PacketDistributor.PacketTarget target, Map<Integer, Integer> paintingRealmPositions)
    {
        Iterable<List<Map.Entry<Integer, Integer>>> paintingsList = Iterables.partition(paintingRealmPositions.entrySet(), MAX_LIST_SIZE);
        int size = Iterables.size(paintingsList);
        LOGGER.debug("Partitioning sync paintings message into " + size + " segments.");
        for (int i = 0; i < size; i++)
        {
            List<Map.Entry<Integer, Integer>> list = Iterables.get(paintingsList, i);
            PainterMessages.INSTANCE.send(target, new SyncPaintingRealmsMessage(i, list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
        }
    }

    public static void encode(SyncPaintingRealmsMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.index);
        buf.writeInt(msg.paintingRealmPositions.size());
        msg.paintingRealmPositions.forEach((id, value) ->
        {
            buf.writeInt(id);
            buf.writeInt(value);
        });
    }

    public static SyncPaintingRealmsMessage decode(PacketBuffer buf)
    {
        int index = buf.readInt();
        int mapSize = buf.readInt();
        Map<Integer, Integer> paintingRealmPositions = new HashMap<>();
        for (int i = 0; i < mapSize; i++)
            paintingRealmPositions.put(buf.readInt(), buf.readInt());
        return new SyncPaintingRealmsMessage(index, paintingRealmPositions);
    }

    @OnlyIn(Dist.CLIENT)
    public int getIndex()
    {
        return index;
    }

    @OnlyIn(Dist.CLIENT)
    public Map<Integer, Integer> getPaintingRealmPositions()
    {
        return paintingRealmPositions;
    }
}
