package io.github.ocelot.painting;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.ocelot.init.PainterMessages;
import io.github.ocelot.network.AddPaintingMessage;
import io.github.ocelot.network.AddPaintingRealmMessage;
import io.github.ocelot.network.RemovePaintingMessage;
import io.github.ocelot.network.SyncPaintingMessage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Ocelot
 */
public class PaintingManagerSavedData extends WorldSavedData implements PaintingManager
{
    private final Map<UUID, Painting> paintings;
    private final BiMap<Integer, Integer> paintingRealmPositions;
    private final Map<Integer, UUID> paintingRealmPositionLookup;
    private int nextId;

    private ServerWorld world;

    public PaintingManagerSavedData()
    {
        super(DATA_NAME);
        this.paintings = new HashMap<>();
        this.paintingRealmPositions = HashBiMap.create();
        this.paintingRealmPositionLookup = new HashMap<>();
        this.nextId = 0;
    }

    @Override
    public boolean initializeRealm(UUID id)
    {
        if (!this.hasPainting(id))
            return false;
        int imageId = Arrays.hashCode(Objects.requireNonNull(this.getPainting(id)).getPixels());
        if (!this.paintingRealmPositions.containsKey(imageId))
        {
            PainterMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new AddPaintingRealmMessage(imageId, this.nextId));
            this.paintingRealmPositions.put(imageId, this.nextId++);
            this.paintingRealmPositionLookup.put(imageId, id);
            return true;
        }
        return this.paintingRealmPositions.containsKey(imageId);
    }

    @Override
    public void addPainting(Painting painting) throws IllegalStateException
    {
        if (FixedPaintingType.isFixed(painting.getId()))
            return;
        if (this.paintings.containsKey(painting.getId()))
            painting.shuffleId();
        painting.setPaintingManager(this);
        this.paintings.put(painting.getId(), painting);
        this.markDirty();
        PainterMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new AddPaintingMessage(painting));
    }

    @Override
    public void removePainting(UUID id)
    {
        if (FixedPaintingType.isFixed(id))
            return;
        this.paintings.remove(id);
        this.markDirty();
        PainterMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new RemovePaintingMessage(id));
    }

    @Override
    public boolean hasPainting(UUID id)
    {
        return FixedPaintingType.isFixed(id) || this.paintings.containsKey(id);
    }

    @Nullable
    @Override
    public Painting getPainting(UUID id)
    {
        return FixedPaintingType.isFixed(id) ? FixedPaintingType.get(id) : this.paintings.get(id);
    }

    @Nullable
    @Override
    public UUID getRealmPainting(ChunkPos pos)
    {
        byte offsetPos = getRealmOffset(pos);
        int xOffset = offsetPos & 1;
        int zOffset = (offsetPos >> 1) & 1;
        if ((pos.z - zOffset) != 0 || (pos.x - xOffset) % (REALM_DISTANCE / 16) != 0)
            return null;
        return this.paintingRealmPositionLookup.get(this.paintingRealmPositions.inverse().getOrDefault((pos.x - xOffset) / (REALM_DISTANCE / 16), -1));
    }

    @Override
    public byte getRealmOffset(ChunkPos pos)
    {
        byte offset = 0;
        if ((pos.x & 1) == 1)
            offset |= 1;
        if ((pos.z & 1) == 1)
            offset |= 1 << 1;
        return offset;
    }

    @Override
    public Collection<Painting> getAllPaintings()
    {
        return this.paintings.values();
    }

    @Override
    public Map<Integer, Integer> getAllPaintingRealms()
    {
        return this.paintingRealmPositions;
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.paintings.clear();
        ListNBT paintingsNbt = nbt.getList("paintings", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < paintingsNbt.size(); i++)
        {
            Painting painting = new Painting(paintingsNbt.getCompound(i));
            painting.setPaintingManager(this);
            this.paintings.put(painting.getId(), painting);
            this.paintingRealmPositionLookup.put(Arrays.hashCode(painting.getPixels()), painting.getId());
        }

        this.paintingRealmPositions.clear();
        ListNBT paintingRealmsNbt = nbt.getList("paintingRealms", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < paintingRealmsNbt.size(); i++)
        {
            CompoundNBT paintingRealmNbt = paintingRealmsNbt.getCompound(i);
            this.paintingRealmPositions.put(paintingRealmNbt.getInt("paintingId"), paintingRealmNbt.getInt("realmId"));
        }

        this.nextId = nbt.getInt("nextId");
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        ListNBT paintingsNbt = new ListNBT();
        this.paintings.values().stream().map(Painting::serializeNBT).forEach(paintingsNbt::add);
        nbt.put("paintings", paintingsNbt);

        ListNBT paintingRealmsNbt = new ListNBT();
        this.paintingRealmPositions.entrySet().stream().map(entry ->
        {
            CompoundNBT paintingRealmNbt = new CompoundNBT();
            paintingRealmNbt.putInt("paintingId", entry.getKey());
            paintingRealmNbt.putInt("realmId", entry.getValue());
            return paintingRealmNbt;
        }).forEach(paintingRealmsNbt::add);
        nbt.put("paintingRealms", paintingRealmsNbt);

        nbt.putInt("nextId", this.nextId);
        return nbt;
    }

    public void setWorld(ServerWorld world)
    {
        this.world = world;
    }

    public void sync(Painting painting)
    {
        this.markDirty();
        PainterMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncPaintingMessage(painting));
    }
}
