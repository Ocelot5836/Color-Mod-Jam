package io.github.ocelot.painting;

import io.github.ocelot.painting.render.WorldPaintingTextureCache;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Ocelot
 */
public class ClientPaintingManager implements PaintingManager
{
    public static final ClientPaintingManager INSTANCE = new ClientPaintingManager();

    private final Map<UUID, Painting> paintings;
    private final Map<Integer, Integer> paintingRealmPositions;

    private ClientPaintingManager()
    {
        this.paintings = new HashMap<>();
        this.paintingRealmPositions = new HashMap<>();
    }

    @Override
    public boolean initializeRealm(UUID id)
    {
        throw new UnsupportedOperationException("Client cannot initialize realms");
    }

    @Override
    public void addPainting(Painting painting) throws IllegalStateException
    {
        throw new UnsupportedOperationException("Client cannot modify paintings");
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

    @Override
    public UUID getRealmPainting(ChunkPos pos)
    {
        throw new UnsupportedOperationException("Client cannot get realm id from chunk pos");
    }

    @Override
    public byte getRealmOffset(ChunkPos pos)
    {
        throw new UnsupportedOperationException("Client cannot get realm offset from chunk pos");
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

    public void receivePaintings(int index, Collection<Painting> paintings)
    {
        if (index == 0)
            this.paintings.clear();
        paintings.forEach(painting -> this.paintings.put(painting.getId(), painting));
    }

    public void receivePainting(Painting painting)
    {
        this.paintings.put(painting.getId(), painting);
        WorldPaintingTextureCache.updateTexture(painting);
    }

    public void receiveAddPainting(Painting painting)
    {
        this.paintings.put(painting.getId(), painting);
    }

    public void receiveRemovePainting(UUID id)
    {
        this.paintings.remove(id);
    }

    public void receivePaintingRealms(int index, Map<Integer, Integer> paintings)
    {
        if (index == 0)
            this.paintingRealmPositions.clear();
        this.paintingRealmPositions.putAll(paintings);
    }

    public void receiveAddPaintingRealm(Integer imageId, int realmId)
    {
        this.paintingRealmPositions.put(imageId, realmId);
    }
}
