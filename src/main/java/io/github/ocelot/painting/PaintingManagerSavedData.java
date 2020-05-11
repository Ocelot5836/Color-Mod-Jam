package io.github.ocelot.painting;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Ocelot
 */
public class PaintingManagerSavedData extends WorldSavedData implements PaintingManager
{
    private final Map<UUID, Painting> paintings;

    public PaintingManagerSavedData()
    {
        super(DATA_NAME);
        this.paintings = new HashMap<>();
    }

    @Override
    public void addPainting(Painting painting) throws IllegalStateException
    {
        if (this.paintings.containsKey(painting.getId()))
            throw new IllegalStateException("Painting with id '" + painting.getId() + "' already exists.");
        this.paintings.put(painting.getId(), painting);
// TODO notify clients
    }

    @Override
    public void removePainting(UUID id)
    {
        this.paintings.remove(id);
// TODO notify clients
    }

    @Override
    public boolean hasPainting(UUID id)
    {
        return this.paintings.containsKey(id);
    }

    @Nullable
    @Override
    public Painting getPainting(UUID id)
    {
        return this.paintings.get(id);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.paintings.clear();
        ListNBT paintingsNbt = nbt.getList("paintings", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < paintingsNbt.size(); i++)
        {
            Painting painting = new Painting(paintingsNbt.getCompound(i));
            this.paintings.put(painting.getId(), painting);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        ListNBT paintingsNbt = new ListNBT();
        this.paintings.values().stream().map(Painting::serializeNBT).forEach(paintingsNbt::add);
        nbt.put("paintings", paintingsNbt);
        return nbt;
    }
}
