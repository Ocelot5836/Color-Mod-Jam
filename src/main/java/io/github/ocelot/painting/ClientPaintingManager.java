package io.github.ocelot.painting;

import io.github.ocelot.painting.render.WorldPaintingTextureCache;

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

    private ClientPaintingManager()
    {
        this.paintings = new HashMap<>();
    }

    @Override
    public void addPainting(Painting painting) throws IllegalStateException
    {
        throw new UnsupportedOperationException("Client cannot modify paintings");
    }

    @Override
    public void removePainting(UUID id)
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
    public Collection<Painting> getAllPaintings()
    {
        return this.paintings.values();
    }

    public void receivePaintings(Collection<Painting> paintings)
    {
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
}
