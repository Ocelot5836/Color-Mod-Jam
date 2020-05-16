package io.github.ocelot.painting;

import io.github.ocelot.init.PainterMessages;
import io.github.ocelot.network.AddPaintingMessage;
import io.github.ocelot.network.RemovePaintingMessage;
import io.github.ocelot.network.SyncPaintingMessage;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
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
    private int nextId;

    private ServerWorld world;

    public PaintingManagerSavedData()
    {
        super(DATA_NAME);
        this.paintings = new HashMap<>();
        this.nextId = 0;
    }

    private void generateBlocks(int dimension, int[] pixels)
    {
        BlockPos pos = PaintingManager.getDimensionPos(dimension);
        int[] heights = new int[Painting.SIZE];
        Arrays.fill(heights, 0);
        for (int x = 0; x < Painting.SIZE; x++)
        {
            for (int y = 0; y < Painting.SIZE; y++)
            {
                if (pixels[y] != -1)
                {
                    heights[x] = Painting.SIZE - y;
                    break;
                }
            }
        }

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int z = 0; z < Painting.SIZE; z++)
        {
            for (int x = 0; x < Painting.SIZE; x++)
            {
                mutable.setPos(pos.getX() + x, pos.getY() + (heights[x] + heights[z]) / 2, pos.getZ() + z);
                this.world.setBlockState(mutable, Blocks.GRASS.getDefaultState());
            }
        }
    }

    @Override
    public void addPainting(Painting painting) throws IllegalStateException
    {
        if (Painting.PLAD_PAINTING.getId().equals(painting.getId()))
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
        if (Painting.PLAD_PAINTING.getId().equals(id))
            return;
        this.paintings.remove(id);
        this.markDirty();
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
            painting.setPaintingManager(this);
            this.paintings.put(painting.getId(), painting);
        }
        this.nextId = nbt.getInt("nextId");
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        ListNBT paintingsNbt = new ListNBT();
        this.paintings.values().stream().map(Painting::serializeNBT).forEach(paintingsNbt::add);
        nbt.put("paintings", paintingsNbt);
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
