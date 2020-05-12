package io.github.ocelot.painting;

import io.github.ocelot.init.PainterMessages;
import io.github.ocelot.network.AddPaintingMessage;
import io.github.ocelot.network.RemovePaintingMessage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Collection;
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
        if (Painting.PLAD_PAINTING.getId().equals(painting.getId()))
            return;
        if (this.paintings.containsKey(painting.getId()))
            throw new IllegalStateException("Painting with id '" + painting.getId() + "' already exists.");
        this.paintings.put(painting.getId(), painting);
        PainterMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new AddPaintingMessage(painting));
    }

    @Override
    public void removePainting(UUID id)
    {
        if (Painting.PLAD_PAINTING.getId().equals(id))
            return;
        this.paintings.remove(id);
        PainterMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new RemovePaintingMessage(id));
    }

    @Override
    public boolean hasPainting(UUID id)
    {
        return Painting.PLAD_PAINTING.getId().equals(id) || this.paintings.containsKey(id);
    }

    @Nullable
    @Override
    public Painting getPainting(UUID id)
    {
        return Painting.PLAD_PAINTING.getId().equals(id) ? Painting.PLAD_PAINTING : this.paintings.get(id);
    }

    @Override
    public Collection<Painting> getAllPaintings()
    {
        return this.paintings.values();
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
