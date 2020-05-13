package io.github.ocelot.painting;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * <p>An entity or tile entity that contains a painting within it.</p>
 *
 * @author Ocelot
 */
public interface PaintingHolder
{
    /**
     * Serializes the painting id into a compound.
     */
    default void serializePainting(CompoundNBT nbt)
    {
        UUID paintingId = this.getPaintingId();
        if (paintingId != null)
            nbt.putUniqueId("paintingId", paintingId);
    }

    /**
     * Reads the painting id from a compound.
     *
     * @param nbt The tag full of data
     * @return The id of the painting
     */
    @Nullable
    default UUID deserializePainting(CompoundNBT nbt)
    {
        return nbt.hasUniqueId("paintingId") ? nbt.getUniqueId("paintingId") : null;
    }

    /**
     * @return The painting this holder contains
     */
    @Nullable
    default Painting getPainting()
    {
        IWorld world = this.getPaintingWorld();
        UUID paintingId = this.getPaintingId();
        return paintingId != null && world != null ? PaintingManager.get(world).getPainting(paintingId) : null;
    }

    /**
     * @return The world this painting is stored in
     */
    @Nullable
    default IWorld getPaintingWorld()
    {
        if (this instanceof Entity)
            return ((Entity) this).getEntityWorld();
        if (this instanceof TileEntity)
            return ((TileEntity) this).getWorld();
        return null;
    }

    /**
     * @return The id of this painting
     */
    @Nullable
    UUID getPaintingId();

    /**
     * Sets the painting of this holder to the specified value.
     *
     * @param paintingId The new painting id value
     */
    void setPainting(@Nullable UUID paintingId);
}
