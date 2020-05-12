package io.github.ocelot.tileentity;

import io.github.ocelot.init.PainterBlocks;
import io.github.ocelot.painting.Painting;
import io.github.ocelot.painting.PaintingHolder;
import io.github.ocelot.painting.PaintingManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author Ocelot
 */
public class EaselTileEntity extends TileEntity implements PaintingHolder
{
    private UUID paintingId;

    public EaselTileEntity()
    {
        super(PainterBlocks.EASEL_TE.get());
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        super.write(nbt);
        this.serializePainting(nbt);
        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        this.paintingId = this.deserializePainting(nbt);
    }

    @Nullable
    @Override
    public UUID getPaintingId()
    {
        return paintingId;
    }

    public void setPainting(@Nullable Painting painting)
    {
        if (this.world != null)
        {
            PaintingManager paintingManager = PaintingManager.get(this.world);
            if (!paintingManager.hasPainting(painting))
                paintingManager.addPainting(painting);
            this.paintingId = painting == null ? null : painting.getId();
            this.markDirty();
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        this.read(pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.write(new CompoundNBT());
    }

}
